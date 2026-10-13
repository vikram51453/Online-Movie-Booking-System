
@echo off
echo Checking Ansible demo through WSL...

wsl.exe -d Ubuntu -- bash -lc "cd /home/vikram/ansible-movie-demo && source .venv/bin/activate && ansible-playbook docker-demo.yml -e ansible_python_interpreter=/home/vikram/ansible-movie-demo/.venv/bin/python"

exit /b %ERRORLEVEL%
