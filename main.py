#!/usr/bin/python3

import codecs
import os
from markdown2 import Markdown
from jinja2 import Environment, PackageLoader
import git
import yaml

md = Markdown(extras=['fenced-code-blocks', 'tables'])
jinja = Environment(loader=PackageLoader('main', 'templates'))


def render(markdown, template_name):
    content = md.convert(markdown)
    template = jinja.get_template(template_name)
    html = template.render(content=content)
    return html


def walk(root_path):
    for root, subdirs, files in os.walk(root_path):
        for file in files:
            if file.endswith('.md') and os.path.basename(root) == 'doc':
                if os.path.relpath(root, root_path) == 'doc':
                    sub_name = ''
                else:
                    sub_name = os.path.basename(os.path.dirname(root))
                yield (sub_name, os.path.splitext(file)[0], os.path.join(root, file))


def save(doc, output_dir):
    """renders html templates with markdown inside and save as an html file
    :param doc:
    :param output_dir: is an absolute path to store file in
    """
    sub_name, doc_name, doc_path = doc

    with codecs.open(doc_path,
                     mode='r',
                     encoding='utf-8') as fd:
        markdown = fd.read()

    if len(markdown) < 1:
        return

    html = render(markdown, 'layout.html')
    new_dir = os.path.join(output_dir, sub_name)
    os.makedirs(new_dir, exist_ok=True)
    with codecs.open(os.path.join(new_dir, doc_name + '.html'),
                     mode='w',
                     encoding='utf-8') as fd:
        fd.write(html)


def walk_branches(repo_path):
    """Walk over all git branches and checkout into it
    :param repo_path: is an absolute path of a git repo
    """
    repo = git.Repo(repo_path)
    
    # 'git fetch -p' for prune. It syncs your local origin with remote.
    # Thus deleted branches in remote repo will not be pulled.
    # Without this option error will be thrown: stderr: 'fatal: Couldn't find remote ref feature/us-calendar-dialog
    repo.git.fetch(prune=True)

    current_branch = repo.head.reference.name
    for ref in repo.remote('origin').refs:
        if ref.remote_head == 'HEAD':
            continue
        branch = ref.remote_head
        try:
            repo.git.checkout(branch)
            repo.remotes.origin.pull(branch)
            yield branch
        except Exception as e:
            print(e)
    repo.git.checkout(current_branch)


def walk_and_save(repo_path, build_path, doc_relative_path, target_name=''):
    branches = []
    for branch in walk_branches(repo_path):
        print(branch)
        walk_and_save_files(repo_path, build_path, doc_relative_path, target_name, branch)
        branches.append(branch)
    return branches


def walk_and_save_files(repo_path, build_path, doc_relative_path, target_name, branch='HEAD'):
    doc_path = os.path.join(repo_path, doc_relative_path)
    for doc in walk(doc_path):
        save(doc, os.path.join(build_path, branch, target_name))
    return [branch]


def walk_site_map(path):
    for root, subdirs, files in os.walk(path):
        for file in files:
            if file.endswith('.html'):
                rel_dir = os.path.relpath(root, path)
                yield (rel_dir, os.path.join(rel_dir, file))


def save_site_map(path, branches):
    map_ = {}

    for branch in branches:
        map_[branch] = {}
        for root, file in walk_site_map(os.path.join(path, branch)):
            p = os.path.dirname(root)
            f = os.path.join(branch, file)
            map_[branch][p] = map_[branch].get(p, []) + [(f, file)]

    flatten_map = {}
    for k, v in map_.items():
        if len(v) != 0:
            flatten_map[k] = v

    template = jinja.get_template('site_map.html')
    html = template.render(map=flatten_map)
    with codecs.open(os.path.join(path, 'index.html'),
                     mode='w',
                     encoding='utf-8') as fd:
        fd.write(html)


def build_docs(config):
    action = {'all_branches': walk_and_save, 'current_branch': walk_and_save_files}

    branches = []
    build_path = config['build']['path']
    mode = config['build']['mode']
    for target in config['targets']:
        repo_path = target['path']
        doc_path = target['docs_path']
        name = target['name']
        print("===={}====".format(name))
        branches.append(action[mode](repo_path, build_path, doc_path, name))

    return list(set([item for sublist in branches for item in sublist]))


def main():
    with codecs.open('./config.yml',
                     mode='w',
                     encoding='utf-8') as fd:
        cfg = yaml.load(fd.read())
        branches = build_docs(cfg)
        save_site_map(cfg['build']['path'], branches)


if __name__ == '__main__':
    main()
