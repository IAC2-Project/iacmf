#!/bin/bash

function check () {
  echo "Check if "$1" is available"
  if ! [[ -x "$(command -v "$1")" ]]; then
    echo "INFO: "$1" is not installed" >&2
    return 1
  fi
}

find . -type f -name '*.war' -exec cp {} /opt/tomcat/latest/webapps \;

check systemctl
if [[ $? -eq 0 ]]; then
  vars=$(cat /etc/environment)
  for f in $vars; do  echo "export $f" >> /opt/tomcat/latest/bin/setenv.sh; done
  systemctl restart tomcat
fi

exit 0
