#!/bin/bash

# reponses au questions de mysql-server-5.5 et de phpmyadmin en utilisant les outils fournis dans le paquage debconf-utils
# setting mysql server answers
echo 'mysql-server mysql-server/root_password password 1234' | sudo debconf-set-selections 
echo 'mysql-server mysql-server/root_password_again password 1234' | sudo debconf-set-selections
# setting phpmyadmin 
echo 'phpmyadmin phpmyadmin/dbconfig-install boolean true' | debconf-set-selections
echo 'phpmyadmin phpmyadmin/app-password-confirm password 1234' | debconf-set-selections
echo 'phpmyadmin phpmyadmin/mysql/admin-pass password 1234' | debconf-set-selections
echo 'phpmyadmin phpmyadmin/mysql/app-pass password 1234' | debconf-set-selections
echo 'phpmyadmin phpmyadmin/reconfigure-webserver multiselect apache2' | debconf-set-selections
# -----------------------------------

# installation de mysql-server, mysql-client, apache2,  php5, libapache2-mod-php5, php5-mysql, phpmyadmin. Dans le meme ordre
sudo apt-get install mysql-server -q -y
sudo apt-get install mysql-client -q -y
sudo apt-get install apache2 -q -y
sudo apt-get install php5 -q -y
sudo apt-get install libapache2-mod-php5 -q -y
sudo apt-get install php5-mysql -q -y
sudo apt-get install phpmyadmin -q -y

echo "Include /etc/phpmyadmin/apache.conf" | sudo tee --append /etc/apache2/apache2.conf
sudo service apache2 restart

