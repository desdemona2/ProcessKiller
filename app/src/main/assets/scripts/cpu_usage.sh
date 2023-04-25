#!/bin/bash

#ps -A -o pid,%cpu,%mem,cmd --sort=-%cpu | head -n 21
ps -A -o pid,name,%cpu,%mem --sort=-%mem | grep -v system | grep -v root | head -n 21