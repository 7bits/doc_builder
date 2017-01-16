clean:
	rm -rf build/

package:
	./main.py

localRun:
	http-server build
