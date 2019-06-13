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

  #rm -Rf .vagrant/
  #vagrant global-status
  #vagrant global-status --prune
  # config.vm.network "forwarded_port", guest: 6379, host: 6379, id: "SENTINEL_MASTER_PORT", auto_correct: true
  # config.vm.network "forwarded_port", guest: 26379, host: 26379, id: "SENTINEL_PORT", auto_correct: true
  # config.vm.network "forwarded_port", guest: 6381, host: 6381, id: "SENTINEL_SLAVE_PORT", auto_correct: true

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

  #config.vm.provision "file", source: "vagrant/toolchains.xml", destination: ".m2/toolchains.xml"
  config.vm.provision "file", source: "vagrant/docker_ubuntu_gpg", destination: "docker_ubuntu_gpg"
  config.vm.provision "shell", inline: <<-SHELL
    echo working directory $(pwd)
    ls -la
    echo '>>>>>>>>>> /etc/apt/sources.list >>>>>>>>>>'
    cat /etc/apt/sources.list
    echo '<<<<<<<<<< /etc/apt/sources.list <<<<<<<<<<'
    #sed -Ei "s#http://.+archive.ubuntu.com/ubuntu#http://${IMAGE_ARG_APT_MIRROR:-mirrors.163.com}/ubuntu#g" /etc/apt/sources.list
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
    
    apt-get -yq install openjdk-8-jdk openjdk-8-jre;
    #update-alternatives --config java
    #update-alternatives --config javac
    if [[ -d /usr/lib/jvm/java-8-openjdk-amd64 ]]; then ln -s /usr/lib/jvm/java-8-openjdk-amd64 /usr/lib/jvm/java-8-openjdk; fi;
    POLICY_DIR="/vagrant/UnlimitedJCEPolicyJDK8";
    if [ ! -f /vagrant/jce_policy-8.zip ]; then ${ARIA2C_DOWNLOAD} --header="Cookie: oraclelicense=accept-securebackup-cookie" -d /vagrant -o jce_policy-8.zip http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip; fi;
    export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
    unzip /vagrant/jce_policy-8.zip;
    mkdir -p /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security;
    cp -f ${POLICY_DIR}/US_export_policy.jar /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/US_export_policy.jar;
    cp -f ${POLICY_DIR}/local_policy.jar /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/local_policy.jar;
    rm -rf ${POLICY_DIR};
    
    wget https://github.com/sormuras/bach/raw/master/install-jdk.sh;
    #wget https://github.com/sormuras/bach/raw/support-older-jdks/install-jdk.sh
    bash install-jdk.sh -F 11 --target $HOME/openjdk11 --workspace $HOME/.cache/install-jdk;
    if [[ -d /home/vagrant/openjdk11 ]]; then ln -s /home/vagrant/openjdk11 /usr/lib/jvm/java-11-openjdk-amd64; fi;
    if [[ -d /home/vagrant/openjdk11 ]]; then ln -s /home/vagrant/openjdk11 /usr/lib/jvm/java-11-openjdk; fi;

    #if [ ! -f /vagrant/apache-maven-3.6.1-bin.zip ]; then ${ARIA2C_DOWNLOAD} -d /vagrant -o apache-maven-3.6.1-bin.zip https://archive.apache.org/dist/maven/maven-3/3.6.1/binaries/apache-maven-3.6.1-bin.zip; fi
    #unzip -qq /vagrant/apache-maven-3.6.1-bin.zip
    #mv apache-maven-3.6.1 /opt/
    #export M2_HOME=/opt/apache-maven-3.6.1
    #export PATH=$M2_HOME/bin:$PATH

    chown -R vagrant:vagrant /home/vagrant

    echo -e '\nexport JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64' >> /home/vagrant/.profile
    echo -e '\nexport M2_HOME=/opt/apache-maven-3.6.1' >> /home/vagrant/.profile
    echo -e '\nexport PATH=${JAVA_HOME}/bin:${M2_HOME}/bin:$PATH' >> /home/vagrant/.profile
    echo -e '\nsudo chmod a+rw /var/run/docker.sock' >> /home/vagrant/.profile

    echo -e '\nexport CI_OPT_MAVEN_BUILD_OPTS_REPO="https://github.com/ci-and-cd/maven-build-opts-opensource"' >> /home/vagrant/.profile
  SHELL
end
