# Documentation builder

Be aware! This script will make git checkouts. Please prepare your local repo to be ready for this actions.

1. Clone backend and ui repositories. Remember their paths.
2. `cp config.template.ini config.ini`
3. Modify `config.ini` with paths that your know from the first step.
4. Check python3 is installed on your computer.
5. `sudo pip3 install -r requirements.txt`
6. `python3 main.py`
7. Copy build folder to some http server(nginx)
8. PROFIT!
