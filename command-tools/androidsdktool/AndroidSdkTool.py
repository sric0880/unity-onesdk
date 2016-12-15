from xml.dom import minidom
from os import path
import re, os, shutil, platform

script_dir = path.dirname(__file__)
command_apktool = path.join(script_dir, 'apktool.jar')
command_manifest_merger = path.join(script_dir, 'manifest-merger-jar-with-dependencies.jar')
if 'Windows' in platform.system():
	jar2Dex = os.sep.join([script_dir, 'dex2jar-2.0', 'd2j-jar2dex.bat'])
else:
	jar2Dex = os.sep.join([script_dir, 'dex2jar-2.0', 'd2j-jar2dex.sh'])
command_baksmali = path.join(script_dir, 'baksmali-2.1.3.jar')

def unpack_apk(target_apkfile):
	unzip_folder,_ = path.splitext(target_apkfile)
	shutil.rmtree(unzip_folder, True)
	
	cmd = "java -jar %s d -o %s %s" % (command_apktool, unzip_folder, target_apkfile)
	print(cmd)
	if os.system(cmd) != 0:
		raise Exception("Failed to unpack apk")

	return unzip_folder

def pack_apk(target_apkfile):
	unzip_folder = path.dirname(target_apkfile)
	cmd = 'java -jar %s b -o %s %s' % (command_apktool, target_apkfile, unzip_folder)
	print(cmd)
	if os.system(cmd) != 0:
		raise Exception("Failed to pack pak")
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

	def manifestAddMetadata(self, name, value):
		xml = minidom.parse(self.manifest_filename)
		application = xml.getElementsByTagName('application')[0]
		metaData = xml.createElement('meta-data')
		metaData.setAttribute('android:name', name)
		metaData.setAttribute('android:value', value)
		application.appendChild(metaData)
		print('add metadata <name: %s value: %s>' % (name, value))
		self._saveAndroidManifest(xml)

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
		if os.system('%s %s -o %s --force' % (jar2Dex, jarName, dexFile)) != 0:
			raise Exception('jar %s to dex failed' % jarName)

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