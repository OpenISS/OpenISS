Quickstart for Open Frameworks
-------------------------------
https://linoxide.com/linux-how-to/install-ffmpeg-centos-7/ :
>yum -y install epel-release  
>rpm --import http://li.nux.ro/download/nux/RPM-GPG-KEY-nux.ro  
>rpm -Uvh http://li.nux.ro/download/nux/dextop/el7/x86_64/nux-dextop-release-0-1.el7.nux.noarch.rpm
>yum -y install ffmpeg ffmpeg-devel

https://centos.pkgs.org/6/linuxtech/librtaudio-4.0.11-4.el6.x86_64.rpm.html :
Create the repository config file /etc/yum.repos.d/linuxtech.repo:
>[linuxtech]  
>name=LinuxTECH  
>baseurl=http://pkgrepo.linuxtech.net/el6/release/  
>enabled=1  
>gpgcheck=1  
>gpgkey=http://pkgrepo.linuxtech.net/el6/release/RPM-GPG-KEY-LinuxTECH.NET

Install librtaudio rpm package:
>yum install librtaudio
