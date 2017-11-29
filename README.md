# MOO-MDP: An Object-Oriented Representation for Cooperative Multiagent Reinforcement Learning
This is the codification used in the <i>IEEE Transactions on Cybernetics</i> paper proposing the Multiagent Object-Oriented approach. You are free to use all or part of the codes here presented for any purpose, provided that the paper is properly cited and the original authors properly credited. All the files here shared come with no warranties.

Paper bib entry: <br><br>
<i>
 @ARTICLE{Silvaetal2018,<br>
  author    = {Silva, Felipe Leno Da and <br>
  			  Ruben Glatt and <br>
               Anna Helena Reali Costa},<br>
  title     = {{MOO-MDP: An Object-Oriented Representation for Cooperative Multiagent Reinforcement Learning}},<br>
  journal   = {IEEE Transactions on Cybernetics}, <br>
  pages     = {},<br>
  year      = {2018},<br>
  doi={xxxx}<br>
 }
  </i>
 <br><br>

Part of this project was built on BURLAP2 (http://burlap.cs.brown.edu/). I included the used Burlap version to avoid incompatibility issues, changes in the codification will be necessary if you use a newer BURLAP version.

This work is an extension of a conference paper (http://ieeexplore.ieee.org/document/7839556/). The journal version and the new codification should be cited/used for most of the purposes.

# Files
The folder <b>goldmine_and_gridworld</b> contains the Java implementation (as an Eclipse project) and the BURLAP source files for the <i>Goldmine</i> and <i>Gridworld</i> domains.

The folder <b>prey_predator</b> contains the Python implementation for the <i>Predator-Prey</i> domain.

The folder <b>experiment_results</b> contains the .csv files which contain the results for our experiments and the MATLAB implementation to read the .csv files and output graphs.

# Quickly Replicating Results

The commands mentioned below should be executed on MATLAB and generate the graphs shown in the paper. Some manual modifications in the style were performed to increase readability. The required .m files are inside the <b>experiment_results</b> folder.

<b>Gridworld and Goldmine:</b>
```matlab
folderCSV = %<path for goldmine or gridworld folders>
initTrial = 1;
endTrial = 70; % 50 for gridworld
useMarkers = true;
generateGraphFromBurlapFile(folderCSV, initTrial, endTrial,useMarkers);
```
<b>Predator-Prey</b>

```matlab
folderCSV = %<path for prey-predator_original folder>
repetitions = 250;
initTrial = 1;
endTrial = repetitions; 
useMarkers = false;
convert_preyPredator(folderCSV,repetitions,useMarkers); %may take a long time to run
generateGraphFromBurlapFile(folderCSV, initTrial, endTrial,useMarkers);
```

# How to use

The folder <b>goldmine_and_gridworld</b> stores the implementations for the <b>Goldmine</b> and <b>Gridworld</b> domains. 

We used Eclipse to run the experiments, hence you can import the folder as a project in Eclipse or import all files (including the burlap jar in the lib folder as an library) in your preferred IDE.

The experiments of our paper are replicated by executing the <b>main</b> method in the <b>ExperimentBRACIS2016</b> class (I recommend executing the VM with the parameters -Xms1024m -Xmx14024m). 

After executing this method, .csv files will be generated with the experiments results, that can be used to print graphs on matlab by executing the file <b>generateGraphFromBurlapFile.m</b>.

For executing experiments in the <i>Predator-Prey</i> domain, the <i>experiment.py</i> file must be executed. We used the PyCharm IDE with an <i>Anaconda</i> environment.

The python codification outputs a .csv file in a different format, hence the <i>convert_preyPredator.m</i> script adapts the output to the correct format.

We advise you to implement your own script to generate graphs, as the matlab file is not very well commented.

# Attention
Our DOO-Q and DQL implementations are highly optimized to execute experiments faster, which means that the memory consumption is huge. If you want to use it to applications or in a pc with low memory resources, you will need to change our implementation.

A huge amount of memory can be saved if the implementation of <b>DOOQPolicy</b> is changed to only store entries on <b>policyMemory</b> in case there is two Q-values tied as the best action. However, if you do so, the experiments will run slower.


# Contact

For any question, please send an email to the first author.


