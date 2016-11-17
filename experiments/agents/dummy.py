# -*- coding: utf-8 -*-
"""
Created on Thu Nov 17 11:07:14 2016
Random agent in HFO
@author: Felipe Leno
"""


import random
import copy

from .agent import Agent

class Dummy(Agent):

    def __init__(self, seed=12345,numAg = 3, port=12345, serverPath = "/home/leno/gitProjects/DOO-Q_extension/bin/"):
        super(Dummy, self).__init__(seed=seed,numAg = numAg, port=port, serverPath = serverPath)
        self.sortFriends = True
   
    def initiate_agent_refs(self,numAg,seed):
        """ Create the references to be executed by experiment.py """
        agents = []
        for i in range(numAg):        
            agents.append(copy.deepcopy(self))
            
        
        return agents
            
    
    def select_action(self, state):
        """ When this method is called, the agent executes an action. """
        if self.hfo.getState()[5] == 1: # State[5] is 1 when the player can kick the ball
            #return random.choice([SHOOT, PASS(team_mate), DRIBBLE])
            return random.choice([self.DRIBBLE,self.DRIBBLE, self.SHOOT,self.SHOOT, self.PASSfar, self.PASSnear])
            #return random.choice([self.SHOOT])
            
        return self.MOVE
        
 
  
    def get_discretized_state(self):
        """Dummy agents understand no states at all"""
        return tuple()
    
    def get_Q_size(self):
        """Returns the size of the QTable"""
        return 0
        
    
    def observe_reward(self,state,action,statePrime,reward):
        pass

 