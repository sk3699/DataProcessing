FROM ubuntu

# create a new directory for your files
RUN mkdir /myfiles

# copy files from the host to the container
COPY . /myfiles/

# set the working directory
WORKDIR /myfiles
