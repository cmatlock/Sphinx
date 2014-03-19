#!/bin/bash

#git the project
git clone https://github.com/Venture-TheSphinxTeam/Sphinx.git

cd Sphinx

MACHINE_TYPE = 'uname -m'

# Install the pre-reqs with a package manager.
if [[ $* == *--no-prereqs* ]]; then
	echo "Prereq installation skipped."
elif command -v apt-get > /dev/null; then
	echo "Installing prereqs with apt-get..."
  	sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10
	echo 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen' | sudo tee /etc/apt/sources.list.d/mongodb.list
	sudo apt-get update
	sudo apt-get install mongodb-10gen
else
	echo "Installing the hard way..."
	if [ ${MACHINE_TYPE} == 'x86_64']; then
		echo "64 bit architecture detected"
		curl -O http://downloads.mongodb.org/linux/mongodb-linux-x86_64-2.4.9.tgz
		tar -zxvf mongodb-linux-x86_64-2.4.9.tgz
		mkdir -p mongodb
		cp -R -n mongodb-linux-x86_64-2.4.9/ mongodb
	else
		echo "32 bit architecture detected"
		curl -O http://downloads.mongodb.org/linux/mongodb-linux-i686-2.4.9.tgz
		tar -zxvf mongodb-linux-i686-2.4.9.tgz
		mkdir -p mongodb
		cp -R -n mongodb-linux-i686-2.4.9/ mongodb
	fi
	mkdir -p /data/db
	chown mongodb /data/db
	sudo service mongodb start
fi


curl -O http://downloads.typesafe.com/play/2.2.2/play-2.2.2.zip
sudo mkdir -p /opt/play
sudo unzip play-2.2.2.zip -d /opt/play
sudo cp -r /opt/play/play-2.2.2.zip/* /opt/play/

echo "export PATH=$PATH:/opt/play" | sudo tee -a /etc/bash.bashrc
export PATH="$PATH:/opt/play"
