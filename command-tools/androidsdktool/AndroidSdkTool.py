from xml.dom import minidom
from os import path
import re, os, shutil, platform, stat
from androidsdktool import jar2dex

script_dir = path.dirname(__file__)
command_apktool = path.join(script_dir, 'apktool.jar')
command_manifest_merger = path.join(script_dir, 'manifest-merger-jar-with-dependencies.jar')
command_baksmali = path.join(script_dir, 'baksmali.jar')

def unpack_apk(target_apkfile):
	unzip_folder,_ = path.splitext(target_apkfile)
	shutil.rmtree(unzip_folder, True)
	
	cmd = "java -jar %s d -o %s %s" % (command_apktool, unzip_folder, target_apkfile)
	print(cmd)
	if os.system(cmd) != 0:
		raise Exception("Failed to unpack apk")

	return unzip_folder

def pack_apk(target_apkfile):
	unzip_folder,_ = path.splitext(target_apkfile)
	cmd = 'java -jar %s b -o %s %s' % (command_apktool, target_apkfile, unzip_folder)
	print(cmd)
	if os.system(cmd) != 0:
		raise Exception("Failed to pack apk")
	shutil.rmtree(unzip_folder, True)

class AndroidSdkTool:

	def __init__(self, base_folder):
		self.base_folder = base_folder
		self.manifest_filename = path.join(base_folder, 'AndroidManifest.xml')

	def changePackageName(self, package_name):
		xml = minidom.parse(self.manifest_filename)
		xml.documentElement.setAttribute('package', package_name)
		print("change package name to %s" % package_name)
		self._saveAndroidManifest(xml)

	def getPackageName(self):
		xml = minidom.parse(self.manifest_filename)
		package_name = xml.documentElement.getAttribute('package')
		print("get package name: %s" % package_name)
		return package_name

	def manifestAddMetadata(self, name, value):
		xml = minidom.parse(self.manifest_filename)
		application = xml.getElementsByTagName('application')[0]
		metaData = xml.createElement('meta-data')
		metaData.setAttribute('android:name', name)
		metaData.setAttribute('android:value', value)
		application.appendChild(metaData)
		print('add metadata <name: %s value: %s>' % (name, value))
		self._saveAndroidManifest(xml)

# java -jar target/manifest-merger-jar-with-dependencies.jar  --main mainAndroidManifest.xml
# --log [VERBOSE, INFO, WARNING, ERROR]
# --libs [path separated list of lib's manifests]
# --overlays [path separated list of overlay's manifests]
# --property [PACKAGE | VERSION_CODE | VERSION_NAME | MIN_SDK_VERSION | TARGET_SDK_VERSION | MAX_SDK_VERSION=value]
# --placeholder [name=value]
# --out [path of the output file]
# I have used this library as follows:
# java -jar target/manifest-merger-jar-with-dependencies.jar --main <path_to_main_manifest> --libs <path_to_libs_manifests_divided by ':'> --out <output_manifest> --log WARNING
	def manifestMerge(self, another_manifest):
		cmd = 'java -jar %s --main %s --libs %s --out %s --log WARNING' % (command_manifest_merger, self.manifest_filename, another_manifest, self.manifest_filename)
		print(cmd)
		if os.system(cmd) != 0:
			raise Exception('Merge androidmanifest.xml error')

	def _saveAndroidManifest(self, xml):
		with open(self.manifest_filename, 'w') as f:
			xml.writexml(f)

	def addJar(self, jarName):
		print('start add jar %s to apk' % jarName)
		## Jar to Dex
		dexFile = os.path.join(os.path.dirname(jarName), 'classes.dex')
		jar2dex.jar2dex('%s -o %s --force' % (jarName, dexFile))

		## Dex to Smali
		if os.system('java -jar %s %s -o %s' % (command_baksmali, dexFile, path.join(self.base_folder, 'smali'))) != 0:
			raise Exception('Dex to smali failed')

		os.remove(dexFile)

	def copyFolder(self, src_folder, des_folder):
		print('Copy %s' % des_folder)
		if not path.exists(src_folder):
			print('%s path not exists!'%src_folder)
			return

		des_folder = path.join(self.base_folder, des_folder)
		if not path.exists(des_folder):
			os.makedirs(des_folder)

		for parent, dirnames, filenames in os.walk(src_folder):
			for filename in filenames:
				file = path.join(parent, filename)
				des_file = path.join(des_folder, file[len(src_folder)+1:])
				print('copy %s to %s' % (file, des_file))
				if not path.exists(path.dirname(des_file)):
					os.makedirs(path.dirname(des_file))
				shutil.copy(file, des_file)