from setuptools import setup

setup(name='onesdk',
      version='1.1',
      scripts=['onesdk'],
      description='onesdk command line tool',
      url='https://github.com/sric0880/unity-onesdk',
      author='sric0880',
      author_email='justgotpaid88@qq.com',
      license='MIT',
      packages=['androidsdktool'],
      package_data={'androidsdktool': ['jar2dex.py', 'dex2jar/lib/*.*', 'apktool.jar', 'baksmali-2.1.3.jar', 'manifest-merger-jar-with-dependencies.jar']},
      data_files=[('/usr/local/onesdk', ['onesdk.conf'])],
      zip_safe=False)