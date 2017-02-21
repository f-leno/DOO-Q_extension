sh stopAll.sh


python experiment.py -a SAQL -it 1 -et 250 -n 3 -t 2000 -p 2 -l ./results/ &
python experiment.py -a SAQL -it 251 -et 500 -n 3 -t 2000 -p 2 -l ./results/ &
python experiment.py -a SAQL -it 501 -et 750 -n 3 -t 2000 -p 2 -l ./results/ &
python experiment.py -a SAQL -it 751 -et 1000 -n 3 -t 2000 -p 2 -l ./results/ &
python experiment.py -a MAQL -it 1 -et 200 -n 3 -t 2000 -p 2 -l ./results/ &
python experiment.py -a MAQL -it 201 -et 400 -n 3 -t 2000 -p 2 -l ./results/ &
python experiment.py -a MAQL -it 401 -et 600 -n 3 -t 2000 -p 2 -l ./results/ &
python experiment.py -a MAQL -it 601 -et 800 -n 3 -t 2000 -p 2 -l ./results/ &
python experiment.py -a MAQL -it 801 -et 1000 -n 3 -t 2000 -p 2 -l ./results/ &
python experiment.py -a DOOQ -it 1 -et 333 -n 3 -t 2000 -p 2 -l ./results/ &
python experiment.py -a DOOQ -it 334 -et 666 -n 3 -t 2000 -p 2 -l ./results/ &
python experiment.py -a DOOQ -it 667 -et 1000 -n 3 -t 2000 -p 2 -l ./results/ &
python experiment.py -a DQL -it 1 -et 333 -n 3 -t 2000 -p 2 -l ./results/ &
python experiment.py -a DQL -it 334 -et 666 -n 3 -t 2000 -p 2 -l ./results/ &
python experiment.py -a DQL -it 667 -et 1000 -n 3 -t 2000 -p 2 -l ./results/ &
python experiment.py -a Dummy -it 1 -et 1000 -n 3 -t 2000 -p 2 -l ./results/ &


