from setuptools import setup

setup(name='onesdk',
      version='1.0',
      scripts=['onesdk.py'],
      description='onesdk command line tool',
      url='',
      author='sric0880',
      author_email='justgotpaid88@qq.com',
      license='MIT',
      packages=['androidsdktool'],
      package_data={'androidsdktool': ['dex2jar-2.0/*.*', 'dex2jar-2.0/lib/*.*', 'apktool.jar', 'baksmali-2.1.3.jar', 'manifest-merger-jar-with-dependencies.jar']},
      zip_safe=False)