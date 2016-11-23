# -*- coding: utf-8 -*-
"""
Created on Nov 3 

@author: Felipe Leno
Environment for the predator-prey domain
"""
import random 
import math
from scipy.spatial import distance
import time
import actions

class PredatorPreyEnvironment(object):
    #Environment Size    
    sizeX = 10
    sizeY = 10
    
    
        
    
    #Default Environment Rewards
    capturedReward = +1
    defaultReward = 0  
    
    
    
    agentVisualDepth = 3
    
    reward = None
    lastTerminal = False
    
    numberAgents = None
    agents = None
    
    agentPositions = None
    preyPositions = None
    
    #Agent class objects
    agents = None
    

    storedInitialPositions = None
    lastEvalEps = 0
    agentActions = None
    
    
    #Controls the state transition (for agents running in parallel)
    completedTransition = None
    
    def __init__(self,numberAgents,agents,evalEpisodes):
        """Prepares the environment class:
            numberAgents = the number of agents in the simulation
            agents = the agents objects (subclass of Agent)
            evalEpisodes = the number of evaluation episodes
            parallelAgents = if the agents are running in parallel
        """
        self.numberAgents = numberAgents
        self.agents = agents
           
        self.agentActions = [None]*numberAgents
        
        self.agentPositions = [None]*numberAgents
        self.storedInitialPositions = [None]*evalEpisodes
        self.preyPositions = [None]
        
        self.build_eval_eps(evalEpisodes)

        
    def act(self,agentID,action):
        """Performs an action.
        This function performs nothing until the last agent chooses its action.
        Then, the state transition is activated"""
        self.agentActions[agentID] = action
        
        
    def step(self,agentID):
        """Waits until all the agents applied their actions and the state transition is completed
           The return is statePrime.action,reward"""              
        statePrime = self.get_state(agentID)           
        reward = self.observe_reward(agentID)        
        action = self.agentActions[agentID]
        
        return statePrime,action,reward
    
    def finish_state_transition(self):
        """Executed when all agents completed their state transition procedures"""
        self.state_transition()        
        #self.agentActions = [None]*self.numberAgents

        
        
    def state_transition(self):
        """Executes the state transition"""
        #Process the prey movement
        preyMove = random.randint(0,actions.preyActions-1)
        offsetX = 0
        offsetY = 0
        if  (preyMove == actions.NORTH):
            offsetX = 0
            offsetY = 1
        elif(preyMove == actions.SOUTH):
            offsetX = 0
            offsetY = -1           
        elif(preyMove == actions.EAST):
            offsetX = 1
            offsetY = 0 
        elif(preyMove == actions.WEST):
            offsetX = -1
            offsetY = 0
            
        self.preyPositions[0] = self.preyPositions[0] + offsetX
        self.preyPositions[1] = self.preyPositions[1] + offsetY
        
        #movements towards walls
        if(self.preyPositions[0] <= 0):
            self.preyPositions[0] = 1
        elif(self.preyPositions[0] > self.sizeX):
            self.preyPositions[0] = self.sizeX
            
        if(self.preyPositions[1] <= 0):
            self.preyPositions[1] = 1
        elif(self.preyPositions[1] > self.sizeY):
            self.preyPositions[1] = self.sizeY
        
        # Move all agents
        agentIndex = 0
        for agentP in self.agentPositions:
            agtMove = self.agentActions[agentIndex]
            
            if  (agtMove == actions.NORTH):
                offsetX = 0
                offsetY = 1
            elif(agtMove == actions.SOUTH):
                offsetX = 0
                offsetY = -1           
            elif(agtMove == actions.EAST):
                offsetX = 1
                offsetY = 0 
            elif(agtMove == actions.WEST):
                offsetX = -1
                offsetY = 0
            self.agentPositions[agentIndex][0] = self.agentPositions[agentIndex][0] + offsetX
            self.agentPositions[agentIndex][1] = self.agentPositions[agentIndex][1] + offsetY
            
            #movements towards walls
            if(self.agentPositions[agentIndex][0] <= 0):
                self.agentPositions[agentIndex][0] = 1
            elif(self.agentPositions[agentIndex][0] > self.sizeX):
                self.agentPositions[agentIndex][0] = self.sizeX
                
            if(self.agentPositions[agentIndex][1] <= 0):
                 self.agentPositions[agentIndex][1] = 1
            elif(self.agentPositions[agentIndex][1] > self.sizeY):
                 self.agentPositions[agentIndex][1] = self.sizeY
            agentIndex += 1
        #Updates the terminal state variable
        self.check_terminal()
        if(self.lastTerminal):
            self.reward = self.capturedReward
        else:
            self.reward = self.defaultReward

            
    def check_terminal(self):
        """Checks if the current state is terminal"""
        caught = False
        agentIndex = 0
        while not caught and agentIndex < self.numberAgents:
            if(self.preyPositions[0] == self.agentPositions[agentIndex][0] and
               self.preyPositions[1] == self.agentPositions[agentIndex][1]):
                   caught = True
            agentIndex += 1
        self.lastTerminal = caught
        
        
    def get_state(self,agentID,sortFriends=True):
        """Returns the state for a given agent"""
        
        if self.lastTerminal:
            return tuple('end')
        #   ret = [0,0]
        #   for i in range(self.numberAgents-1):
        #       ret.append(float('inf'))
        #       ret.append(float('inf'))
        #   return tuple(ret)
        
        selfP = self.agentPositions[agentID]
        selfx = selfP[0]
        selfy = selfP[1]

        preyx = self.preyPositions[0]        
        preyy = self.preyPositions[1]
        #sensations related to the prey
        offsetX = preyx - selfx
        offsetY = preyy - selfy
        
        preySensation = [float('inf'),float('inf')]
        #If the prey is inside the visualDepth
        if(math.fabs(offsetX)<= self.agentVisualDepth and math.fabs(offsetY)<= self.agentVisualDepth):
            preySensation =[offsetX,offsetY]
        
        sensations = preySensation
        #sensations related to other agents
        otherAg = [x for i,x in enumerate(self.agentPositions) if i!=agentID]
        
        #sort agents by distance
        if sortFriends:
            sortedAg = sorted(otherAg,key=lambda i: distance.euclidean(i,selfP))
        else:
            sortedAg = otherAg
        
        notUsedAg = 0
        for i in range(self.numberAgents-1):
             offsetX = sortedAg[i][0] - selfx
             offsetY = sortedAg[i][1] - selfy
             if(math.fabs(offsetX)<= self.agentVisualDepth and math.fabs(offsetY)<= self.agentVisualDepth):
                 sensations.append(offsetX)
                 sensations.append(offsetY)
             else:
                 notUsedAg += 1
        
        #Agents outside the visual depth
        for i in range(notUsedAg):
            sensations.append(float('inf'))
            sensations.append(float('inf'))
            
        
        
        sensations = tuple(sensations)
        return sensations
             
        
        
             
        
    def observe_reward(self,agentID):
        """Returns the reward for the agent"""
        return self.reward
        
    def is_terminal_state(self):
        return self.lastTerminal
        
    def start_evaluation_episode(self):
        """Start next evaluation episode"""
        randomState = random.getstate()
        
            
        epInfo = self.storedInitialPositions[self.lastEvalEps]
        self.load_episode(epInfo)
        #Prepares pointer for the next episode
        self.lastEvalEps = (self.lastEvalEps + 1) % len(self.storedInitialPositions)
        
        
        #Agents in random position
        notChosen = range(len(self.agentPositions))

        agPosic = []        
        while len(notChosen) != 0:
            index = random.choice(notChosen)
            agPosic.append(self.agentPositions[index])
            notChosen.remove(index)
        
        self.agentPositions = agPosic
        
        random.setstate(randomState)
            
                
        
        
        
    def start_learning_episode(self):
        """Starts episode with random initial positions"""
        epInfo = self.generate_episode_information()
        #epInfo = random.choice(self.storedInitialPositions) # ---
        self.load_episode(epInfo)
          

        
    def load_episode(self,episodeInfo):
        """Starts an episode geerated by the generate_episode_information() method"""
        self.preyPositions = episodeInfo[0]
        self.agentPositions = episodeInfo[1]
        self.reward = None #No last step reward
        self.lastTerminal = False
        
    def build_eval_eps(self,numEps):
        """Prepares the evaluation episodes for posterior use"""
        #self.storedInitialPositions = [[[5,5],[[1,1],[10,10],[1,10]]]]        
        for i in range(numEps):        
            self.storedInitialPositions[i] = self.generate_episode_information()
            
            
    def generate_episode_information(self):
        """Generates a random Episode"""
        xprey = random.randint(1,self.sizeX)
        yprey = random.randint(1,self.sizeY)
        #Prey random initial Position        
        preyP = [xprey,yprey]
        
        allAgentsP = []
        #Agents random initial position
        for i in range(self.numberAgents):
            xagent = random.randint(1,self.sizeX)
            yagent = random.randint(1,self.sizeY)
            #No terminal state is generated
            while xagent == xprey and yagent == yprey:
                xagent = random.randint(1,self.sizeX)
                yagent = random.randint(1,self.sizeY)
            agentP = [xagent,yagent]
            allAgentsP.append(agentP)

        return [preyP,allAgentsP]
            
        
            
            
        
    
    
        
        
        