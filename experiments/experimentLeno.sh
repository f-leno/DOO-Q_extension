pkill -f  sh\ ru* 
pkill -f python\ exp*
pkill -f python\ /home/leno/DOO*
killall -9 rcssserver

sh runSimpleExpServer.sh 22445 50 &
sleep 5
sh runSimpleExpAgent.sh 22445 Dummy 1 50 &


