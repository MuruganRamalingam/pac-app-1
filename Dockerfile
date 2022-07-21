# FROM amazon linux:2018.03.0.20220609.0 as graalvm
FROM amazonlinux:2018.03.0.20220609.0 as graalvm
# install amazon cli and java to amazon linux.
ENV LANG=en_US.UTF-8
RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
RUN yum install -y java-1.8.0-openjdk-devel unzip wget git
RUN unzip awscliv2.zip
RUN ./aws/install
RUN aws --version
#install java to amazon linux.
ENV JAVA_HOME /usr/lib/jvm/jre-1.8.0-openjdk.x86_64/
ENV PATH ${JAVA_HOME}/bin:${PATH}
RUN export JAVA_HOME
RUN export PATH
RUN java -version
#install gradle to amazon linux.
RUN wget https://downloads.gradle-dn.com/distributions/gradle-5.1.1-bin.zip -P /tmp
RUN unzip -d /opt/gradle /tmp/gradle-5.1.1-bin.zip
RUN ls /opt/gradle/gradle-5.1.1
RUN echo 'export GRADLE_HOME=/opt/gradle/gradle-5.1.1' >> /etc/profile.d/gradle.sh
RUN echo 'export PATH=${GRADLE_HOME}/bin:${PATH}'  >> /etc/profile.d/gradle.sh
RUN chmod +x /etc/profile.d/gradle.sh
RUN source /etc/profile.d/gradle.sh
ENV GRADLE_HOME /opt/gradle/gradle-5.1.1
ENV PATH ${GRADLE_HOME}/bin:${PATH}
RUN export JAVA_HOME
RUN export GRADLE_HOME
RUN export PATH
RUN gradle -v
#BUILD SRC
RUN mkdir -p /home/test/
WORKDIR /home/test/
RUN git clone https://github.com/zhoujiuzhou9/pac-app.git
WORKDIR /home/test/pac-app/
RUN gradle build --no-daemon
#install graalvm to amazon linux.
WORKDIR /
RUN yum install -y gcc gcc-c++ libc6-dev  zlib1g-dev curl bash zlib zlib-devel zip  \
    && rm -rf /var/cache/yum
ENV GRAAL_VERSION 1.0.0-rc13
ENV GRAAL_FILENAME graalvm-ce-${GRAAL_VERSION}-linux-amd64.tar.gz
RUN export PATH
RUN curl -4 -L https://github.com/oracle/graal/releases/download/vm-${GRAAL_VERSION}/${GRAAL_FILENAME} -o /tmp/${GRAAL_FILENAME}
RUN tar -zxvf /tmp/${GRAAL_FILENAME} -C /tmp \
    && mv /tmp/graalvm-ce-${GRAAL_VERSION} /usr/lib/graalvm
RUN rm -rf /tmp/*
CMD ["/usr/lib/graalvm/bin/native-image"]
RUN mkdir -p /home/application/
RUN cp -rf /home/test/pac-app/* /home/application/
RUN chmod 777 -R /home/application/*
#build-native-image
WORKDIR /home/application
RUN /home/application/build-native-image.sh
RUN chmod 755 bootstrap
RUN chmod 755 server
RUN zip -j function.zip bootstrap libsunec.so cacerts server
RUN rm -rf /home/application/*
RUN rm -rf /home/test/pac-app/*
#end
EXPOSE 8080
ENTRYPOINT ["/home/application/server"]

