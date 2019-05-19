from setuptools import setup, find_packages

setup(
    name='sfdc_get_user',
    version='1.0.0',
    packages=find_packages(),
    package_data={},
    url='',
    license='MIT',
    author='nsuhara',
    author_email='na010210dv@gmail.com',
    install_requires=['simple-salesforce==0.74.2'],
    long_description='sample rest api for sfdc',
)
