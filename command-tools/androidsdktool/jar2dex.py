import os

def jar2dex(args):
	script_dir = os.path.dirname(__file__)
	libpath = os.path.join(script_dir, 'dex2jar/lib')
	jarlibs = os.listdir(libpath)
	fullpathlibs = [os.path.join(libpath, jar) for jar in jarlibs]
	classpath = ':'.join(fullpathlibs)
	cmd = 'java -Xms512m -Xmx1024m -classpath "%s" "com.googlecode.dex2jar.tools.Jar2Dex" %s' % (classpath, args)
	print(cmd)
	if os.system(cmd) != 0:
		raise Exception("Jar to dex failed.")

