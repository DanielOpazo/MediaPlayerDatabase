#!/usr/bin/env python3

# *****************************************************************************
# Description:
#  This script is meant to run the Pairing listender to allow theu ser to push
#  a button of the piface to allow the Android app for the Media Center to pair
#  with it. 
#
# Date Created:
#  November 21 2015
#
# Author(s):
#  Samson Truong      100848346
#  Daniel Opazo Baer  100857806
#  Kais Hassanali     100861319
# *****************************************************************************

# *****************************************************************************
# Imported Modules
# *****************************************************************************

import RPi.GPIO as GPIO
import socket
import sys
from time import sleep

# *****************************************************************************
# Global Variable & Constant Declarations
# *****************************************************************************

PAIR_GPIO = 25
PAIR_PORT = 3000
BUFFER_SIZE = 100

# *****************************************************************************
# Function Definitions
# *****************************************************************************

# *****************************************************************************
# Description:
#  Following function is used to poll the pair button, and if it is clicked,
#  we will open up a socket to listen for the Android app pairing.
#
# Parameters: 
#  NONE
#
# Returns:
#  NONE
#
# Date Created:
#  November 21 2015
# *****************************************************************************
def pollPairButton(clientIP):

    GPIO.setmode(GPIO.BCM)                  # initialise RPi.GPIO
    GPIO.setup(PAIR_GPIO, GPIO.IN, pull_up_down=GPIO.PUD_UP)  # as inputs pull-ups high
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind((socket.gethostbyname(clientIP), PAIR_PORT))
    
    while (True):
        sleep(1)
        while not GPIO.input(PAIR_GPIO):
            print ("pressed")
            data, addr = sock.recvfrom(BUFFER_SIZE) # grab packet
            sock.sendto("authorize".encode(), addr)
                        
# end of function definition

# *****************************************************************************
# Main Handler
# *****************************************************************************

if __name__ == "__main__":

    if len(sys.argv) < 1:
        print ("incorrect number of arguments")
        exit()
    else:
        clientIP = sys.argv[1]
        pollPairButton(clientIP)

# end of main handler
