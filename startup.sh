#!/bin/bash
set -e
# Function to check for Java and Maven installations
function check_java_maven_installations() {
    # Check for Java
    if command -v java &>/dev/null; then
        echo "Java is already installed."
    else
        echo "Java is not installed. Please install Java 11 and try again."
#        read -p "Do you want to install Java 11 now? (y/n): " ans
#        if [[ "$ans" = [yY] ]]; then
#          echo "Installing Java 11..."
#          sudo apt-get update
#          sudo apt-get install -y openjdk-11-jdk
#        else
#          echo "Sorry!!! Java is necessary to run this application."
         exit 1
#        fi
    fi

    # Check for Maven
    if command -v mvn &>/dev/null; then
        echo "Maven is already installed."
    else
        echo "Maven is not installed. Please install Maven and try again."
#        read -p "Do you want to install Maven now? (y/n): " ans
#        if [[ "$ans" = [yY] ]]; then
#          echo "Installing Maven..."
#          sudo apt-get update
#          sudo apt-get install -y maven
#        else
#          echo "Sorry!!! Maven is necessary to run this application."
         exit 1
#        fi
    fi
}

function starting_CPS () {
    cd target
    if [ -f "channel-processing-system-1.0-SNAPSHOT.jar" ]; then
        echo -e "\nStarting Channel Processing System, please wait!"
        java -jar channel-processing-system-1.0-SNAPSHOT.jar
    else
        echo -e "\nChannel Processing Snapshot is not available. Please rebuild.\n"
    fi
}


# Call the function
check_java_maven_installations

if [ -d "target" ]; then
    echo -e "target folder is available!"
    read -p "Do you want to rebuild (y/n)?" YorN
    if [ "$YorN" = "y" ]; then
        echo -e "\nBuilding Channel Processing System! Please wait..."
        mvn clean install &>/dev/null
        if [ "$?" -eq 0 ]; then
            starting_CPS
        else
            echo -e "Build Failed! Please check and run startup script again. \n"
            exit 1
        fi
    elif [ "$YorN" = "n" ]; then
        starting_CPS
    else
        echo -e "Please provide valid input. Try starting script again.\n"
        exit 0
    fi
else
    echo -e "Building Channel Processing System! Please wait. \n"
    mvn clean install &>/dev/null
    if [ "$?" -eq 0 ]; then
        starting_CPS
    else
        echo -e "Build Failed! Please check and run startup script again. \n"
        exit 1
    fi
fi