# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.
Vagrant.configure("2") do |config|
  # The most common configuration options are documented and commented below.
  # For a complete reference, please see the online documentation at
  # https://docs.vagrantup.com.

  # Every Vagrant development environment requires a box. You can search for
  # boxes at https://vagrantcloud.com/search.
  # https://github.com/geerlingguy/packer-ubuntu-1804
  config.vm.box = "geerlingguy/ubuntu1804"

  # Disable automatic box update checking. If you disable this, then
  # boxes will only be checked for updates when the user runs
  # `vagrant box outdated`. This is not recommended.
  config.vm.box_check_update = false

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine. In the example below,
  # accessing "localhost:8080" will access port 80 on the guest machine.
  # NOTE: This will enable public access to the opened port
  # config.vm.network "forwarded_port", guest: 80, host: 8080
  config.vm.network "forwarded_port", guest: 6379, host: 6379
  config.vm.network "forwarded_port", guest: 26379, host: 26379
  config.vm.network "forwarded_port", guest: 6381, host: 6381

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine and only allow access
  # via 127.0.0.1 to disable public access
  # config.vm.network "forwarded_port", guest: 80, host: 8080, host_ip: "127.0.0.1"

  # Create a private network, which allows host-only access to the machine
  # using a specific IP.
  config.vm.network "private_network", ip: "192.168.33.10"

  # Create a public network, which generally matched to bridged network.
  # Bridged networks make the machine appear as another physical device on
  # your network.
  # config.vm.network "public_network"

  # use default ~/.vagrant.d/insecure_private_key
  # `vagrant ssh` or `ssh -i ~/.vagrant.d/insecure_private_key vagrant@192.168.33.10`
  config.ssh.insert_key = false

  # Share an additional folder to the guest VM. The first argument is
  # the path on the host to the actual folder. The second argument is
  # the path on the guest to mount the folder. And the optional third
  # argument is a set of non-required options.
  # config.vm.synced_folder "../data", "/vagrant_data"

  # Provider-specific configuration so you can fine-tune various
  # backing providers for Vagrant. These expose provider-specific options.
  # Example for VirtualBox:
  #
  # config.vm.provider "virtualbox" do |vb|
  #   # Display the VirtualBox GUI when booting the machine
  #   vb.gui = true
  #
  #   # Customize the amount of memory on the VM:
  #   vb.memory = "1024"
  # end
  #
  # View the documentation for the provider you are using for more
  # information on available options.
  config.vm.provider "virtualbox" do |vb|
    vb.memory = "2048"
  end

  # default synced folder
  config.vm.synced_folder ".", "/vagrant"
  config.vm.synced_folder "~/.m2", "/home/vagrant/.m2",
    owner: "vagrant", group: "vagrant", mount_options: ["uid=900", "gid=900"]

  # Enable provisioning with a shell script. Additional provisioners such as
  # Puppet, Chef, Ansible, Salt, and Docker are also available. Please see the
  # documentation for more information about their specific syntax and use.
  # config.vm.provision "shell", inline: <<-SHELL
  #   apt-get update
  #   apt-get install -y apache2
  # SHELL

  config.vm.provision "file", source: "vagrant/toolchains.xml", destination: ".m2/toolchains.xml"
  config.vm.provision "file", source: "vagrant/docker_ubuntu_gpg", destination: "docker_ubuntu_gpg"
  config.vm.provision "shell", inline: <<-SHELL
    echo working directory $(pwd)
    ls -la
    echo '>>>>>>>>>> /etc/apt/sources.list >>>>>>>>>>'
    cat /etc/apt/sources.list
    echo '<<<<<<<<<< /etc/apt/sources.list <<<<<<<<<<'
    sed -Ei "s#http://.+archive.ubuntu.com/ubuntu#http://${IMAGE_ARG_APT_MIRROR:-mirrors.163.com}/ubuntu#g" /etc/apt/sources.list
    apt-get update
    apt-get -y install apt-transport-https aria2 ca-certificates curl git gnupg2 net-tools software-properties-common unzip

    #curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
    apt-key add docker_ubuntu_gpg
    #add-apt-repository "deb [arch=amd64] https://mirrors.tuna.tsinghua.edu.cn/docker-ce/linux/ubuntu $(lsb_release -cs) stable"
    add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
    #add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) edge"
    #add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) nightly"
    sed -i "s#https://download.docker.com/linux/ubuntu#https://${IMAGE_ARG_APT_MIRROR:-mirrors.tuna.tsinghua.edu.cn}/docker-ce/linux/ubuntu#g" /etc/apt/sources.list
    echo '>>>>>>>>>> /etc/apt/sources.list >>>>>>>>>>'
    cat /etc/apt/sources.list
    echo '<<<<<<<<<< /etc/apt/sources.list <<<<<<<<<<'
    apt-get -y update
    apt-get -y remove docker docker-engine docker.io
    apt-cache madison docker-ce
    apt-get -yq install docker-ce
    service docker start
    if [ ! -f /home/vagrant/.docker/config.json ]; then mkdir -p /home/vagrant/.docker; touch /home/vagrant/.docker/config.json; echo '{}' > /home/vagrant/.docker/config.json; fi

    export ARIA2C_DOWNLOAD="aria2c --file-allocation=none -c -x 10 -s 10 -m 0 --console-log-level=notice --log-level=notice --summary-interval=0"
    # 'http://download.oracle.com/otn-pub/java/jdk/8u171-b11/512cd62ec5174c3487ac17c61aaa89e8/jdk-8u171-linux-x64.tar.gz' is no longer exists
    if [ ! -f /vagrant/jdk-8u181-linux-x64.tar.gz ]; then ${ARIA2C_DOWNLOAD} --header="Cookie: oraclelicense=accept-securebackup-cookie" -d /vagrant -o jdk-8u181-linux-x64.tar.gz http://download.oracle.com/otn-pub/java/jdk/8u181-b13/96a7b8442fe848ef90c96a2fad6ed6d1/jdk-8u181-linux-x64.tar.gz; fi
    export JAVA_HOME=/usr/lib/jvm/java-8-oracle
    mkdir -p $(dirname ${JAVA_HOME})
    tar -xzf /vagrant/jdk-8u181-linux-x64.tar.gz -C $(dirname ${JAVA_HOME})
    mv $(dirname ${JAVA_HOME})/jdk1.8.0_181 ${JAVA_HOME}
    export PATH="${JAVA_HOME}/bin:$PATH"
    export JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo JAVA_VERSION ${JAVA_VERSION}
    mkdir -p /Library/Java/JavaVirtualMachines/jdk${JAVA_VERSION}.jdk/Contents
    ln -s ${JAVA_HOME} /Library/Java/JavaVirtualMachines/jdk${JAVA_VERSION}.jdk/Contents/Home
    ln -s ${JAVA_HOME} /usr/lib/jvm/java-8-openjdk-amd64
    POLICY_DIR="UnlimitedJCEPolicyJDK8"
    if [ ! -f /vagrant/jce_policy-8.zip ]; then ${ARIA2C_DOWNLOAD} --header="Cookie: oraclelicense=accept-securebackup-cookie" -d /vagrant -o jce_policy-8.zip http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip; fi
    unzip /vagrant/jce_policy-8.zip
    cp -f ${POLICY_DIR}/US_export_policy.jar ${JAVA_HOME}/jre/lib/security/US_export_policy.jar
    cp -f ${POLICY_DIR}/local_policy.jar ${JAVA_HOME}/jre/lib/security/local_policy.jar
    rm -rf ${POLICY_DIR}

    if [ ! -f /vagrant/apache-maven-3.5.4-bin.zip ]; then ${ARIA2C_DOWNLOAD} -d /vagrant -o apache-maven-3.5.4-bin.zip https://archive.apache.org/dist/maven/maven-3/3.5.4/binaries/apache-maven-3.5.4-bin.zip; fi
    unzip -qq /vagrant/apache-maven-3.5.4-bin.zip
    mv apache-maven-3.5.4 /opt/
    export M2_HOME=/opt/apache-maven-3.5.4
    export PATH=$M2_HOME/bin:$PATH

    chown -R vagrant:vagrant /home/vagrant

    echo -e '\nexport JAVA_HOME=/usr/lib/jvm/java-8-oracle' >> /home/vagrant/.profile
    echo -e '\nexport M2_HOME=/opt/apache-maven-3.5.4' >> /home/vagrant/.profile
    echo -e '\nexport PATH=${JAVA_HOME}/bin:${M2_HOME}/bin:$PATH' >> /home/vagrant/.profile
    echo -e '\nsudo chmod a+rw /var/run/docker.sock' >> /home/vagrant/.profile
  SHELL
end
