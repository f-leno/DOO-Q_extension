# -*- coding: utf-8 -*-
"""
Created on Thu Nov 17 11:06:45 2016
Agent Class for evaluation
@author: Felipe Leno
"""



import abc

class Agent(object):
    """ This is the base class for all agent implementations.

    """
    __metaclass__ = abc.ABCMeta
    
    environment = None

    
    numAg = None
    sortFriends = None
    
    references = None
    seed = None
    agentIndex = None
    
    def __init__(self, seed=12345,numAg = 3):
        """ Initializes an agent for a given environment. """
        
        self.seed = seed
        self.exploring = True
        self.training_steps_total = 0
        self.numAg = numAg
        self.references = self.initiate_agent_refs(numAg,seed)
        #self.hfo = HFOEnvironment()

        # set the agent seed
        #random.seed(seed)

    def getAgRef(self,agentIndex):
        """Returns the reference for the agent to be executed"""
        return self.references[agentIndex]
       
    def connectEnv(self,environment,agentIndex):
        """Connects to the prey-predator environment"""
        self.environment = environment
        self.agentIndex = agentIndex
        
        
    @abc.abstractmethod
    def initiate_agent_refs(self,numAg,seed):
        """ Create the references to be executed by experiment.py """
        pass
    @abc.abstractmethod
    def select_action(self, state,agentIndex):
        """ When this method is called, the agent executes an action. """
        pass
    @abc.abstractmethod
    def get_proc_state(self,agentIndex):
        """ Returns a processed version of the current state """
        pass
    def action(self,agentIndex):
        """Defines the action to be executed and returns the action and the 
        state in the point of view of the agent"""
        state = self.get_proc_state(agentIndex)
        action = self.select_action(state,agentIndex)
        self.environment.act(agentIndex,action)
        return state,action
    
      
        
    @abc.abstractmethod
    def observe_reward(self,state,action,statePrime,reward,agentIndex):
        """ After executing an action, the agent is informed about the state-action-reward-state tuple """
        pass
    @abc.abstractmethod
    def get_Q_size(self,agentIndex):
        """Returns the size of the QTable"""
        pass

    
    def step(self):
        """ Perform a complete training step """
        status = self.hfo.step()
        return status,self.get_discretized_state()
        

    def set_exploring(self, exploring):
        """ The agent keeps track if it should explore in the current state (used for evaluations) """
        self.exploring = exploring


            


     
    def finish_training(self):
        """End the training"""
        pass