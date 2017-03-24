all:
	./main.py

install:
	pip3 install -r requirements.txt

server:
	http-server build
