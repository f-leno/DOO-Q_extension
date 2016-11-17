#!/usr/bin/env python
# encoding: utf-8

# Before running this program, first Start HFO server:
# $> ./bin/HFO --offense-agents 1

import argparse
import sys
import os
import csv
import random, itertools
from hfo import *
from threading import Thread
from time import sleep
#from cmac import CMAC


from agents.agent import Agent
from statespace_util import *




def get_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('-n','--number_agents',type=int, default=3)
    parser.add_argument('-a','--agent',  default='Dummy') #Here, one agent class controls everything
    parser.add_argument('-t','--learning_trials',type=int, default=10)
    parser.add_argument('-i','--evaluation_interval',type=int, default=5)
    parser.add_argument('-d','--evaluation_duration',type=int, default=5)
    parser.add_argument('-s','--seed',type=int, default=12345)
    parser.add_argument('-l','--log_file',default='/home/leno/gitProjects/DOO-Q_extension/log/')
    parser.add_argument('-p','--port',type=int, default=12345)
    parser.add_argument('-r','--number_trial',type=int, default=1)
    parser.add_argument('-e','--server_path',  default='/home/leno/gitProjects/DOO-Q_extension/bin/')
    return parser.parse_args()


def build_agents():
    """Builds and returns the agent object as specified by the arguments"""
    agent = None
    
    
    parameter = get_args()
    

    agentName = getattr(parameter,"agent")
    print "AgentName: "+agentName
    try:
        AgentClass = getattr(
           __import__('agents.' + (agentName).lower(),
                      fromlist=[agentName]),
                      agentName)
    except ImportError:
           sys.stderr.write("ERROR: missing python module: " +agentName + "\n")
           sys.exit(1)
    
    print "Creating agent"
    AGENT = AgentClass(seed=parameter.seed, numAg = parameter.number_agents,port=parameter.port, serverPath = parameter.server_path)
    print "OK Agent"
    agent = AGENT
        
    return agent
    

def main():
    parameter = get_args()
    print parameter
    print('***** Loading agent implementations')
    agent = build_agents()
    #print('***** %s: %s Agent online' % (str(AGENT.unum), str(parameter.agent)))
    print('***** %s: Agents online --> %s')
   # print('***** %s: Agents online --> %s' % (str(AGENT.unum), str(AGENT)))
   # print('***** %s: Setting up train log files' % str(AGENT.unum))
    #train_csv_file = open(parameter.log_file + "_" + str(AGENT.unum) + "_train", "wb")
    print "Agent Classes OK"
    #Initiate agent Threads    
    #global okThread
    #okThread = True
    
    #agentThreads = []
    random.seed(parameter.seed)
    #try:
        #Initiating agent
    agentRefs = [] 
    agentThreads = []
    for i in range(parameter.number_agents):
            agentRefs.append(agent.getAgRef(i))
            
    for i in range(parameter.number_agents):
        agentThreads.append(Thread(target = thread_agent, args=(agentRefs[i],agentRefs,i,parameter)))
        agentThreads[i].start()
        sleep(3)
        
    for i in range(parameter.number_agents):
        agentThreads[i].join()
            
            
    #except Exception as e:
    #    print e.__doc__
    #    print e.message
    #    okThread = False
   
    

    

    
    
def thread_agent(agentObj,allAgents,agentIndex,mainParameters):
    """This method is executed by each thread in the system and corresponds to the control
    of one playing agent"""
    
    logFolder = mainParameters.log_file + getattr(mainParameters,"agent")+"/_0_"+str(mainParameters.number_trial)+"_AGENT_"+str(agentIndex+1)+"_RESULTS"
    
    #Connecting agent to server 
    print "******Connecting agent "+str(agentIndex)+"****"
    agentObj.connectHFO()
    #Building Log folder name
    print "******Connected agent "+str(agentIndex)+"****"
    
    print logFolder + "********" 
    train_csv_file = open(logFolder + "_train", "wb")
    train_csv_writer = csv.writer(train_csv_file)
    train_csv_writer.writerow(("trial","frames_trial","goals_trial","used_budget"))
    train_csv_file.flush()
    print('***** %s: Setting up eval log files' % str(agentIndex))
    #eval_csv_file = open(parameter.log_file + "_" + str(AGENT.unum) + "_eval", "wb")
    eval_csv_file = open(logFolder + "_eval", "wb")
    eval_csv_writer = csv.writer(eval_csv_file)
    eval_csv_writer.writerow(("trial","goal_percentage","avg_goal_time","used_budget"))
    eval_csv_file.flush()
    
    print('***** %s: Start training' % str(agentIndex))
    for trial in range(0,mainParameters.learning_trials+1):
        # perform an evaluation trial
        if(trial % mainParameters.evaluation_interval == 0):
            #print('***** %s: Running evaluation trials' % str(AGENT.unum) )
            agentObj.set_exploring(False)
            goals = 0.0
            time_to_goal = 0.0
            for eval_trials in range(1,mainParameters.evaluation_duration+1):
                eval_frame = 0
                eval_status = agentObj.IN_GAME
                while eval_status == agentObj.IN_GAME:
                    eval_frame += 1
                    agentObj.applyAction()
                    
                    eval_status, statePrime = agentObj.step()
                    if(eval_status == agentObj.GOAL):
                        goals += 1.0
                        time_to_goal += eval_frame
                        #print('********** %s: GGGGOOOOOOOOOOLLLL: %s in %s' % (str(AGENT.unum), str(goals), str(time_to_goal)))
            goal_percentage = 100*goals/mainParameters.evaluation_duration
            #print('***** %s: Goal Percentage: %s' % (str(AGENT.unum), str(goal_percentage)))
            if (goals != 0):
                avg_goal_time = time_to_goal/goals
            else:
                avg_goal_time = 0.0
            #print('***** %s: Average Time to Goal: %s' % (str(AGENT.unum), str(avg_goal_time)))
            # save stuff
            eval_csv_writer.writerow((trial,"{:.2f}".format(goal_percentage),"{:.2f}".format(avg_goal_time),str(agentObj.get_Q_size())))
            eval_csv_file.flush()
            agentObj.set_exploring(True)
            # reset agent trace
            
           
        
        #print('***** %s: Starting Learning Trial %d' % (str(AGENT.unum),trial))
        status = agentObj.IN_GAME
        frame = 0
        while status == agentObj.IN_GAME:
            frame += 1
            state = agentObj.get_discretized_state()
            action = agentObj.applyAction()
            status, statePrime = agentObj.step()
            reward = agentObj.get_reward(status)
            agentObj.observe_reward(state,action,statePrime,reward)
        #print('***** %s: Trial ended with %s'% (str(AGENT.unum), AGENT.hfo.statusToString(status)))
        #print('***** %s: Agent --> %s'% (str(AGENT.unum), str(AGENT)))
        
        # save stuff
        train_csv_writer.writerow((trial,frame,reward,str(agentObj.get_Q_size())))
        train_csv_file.flush()
        #agentObj.stateActionTrace = {}



        # Quit if the server goes down
        if status == agentObj.SERVER_DOWN:
            #agentObj.hfo.act(QUIT)
            agentObj.quit()
            print('***** %s: Shutting down agent' % str(agentIndex))
            break
    print('***** %s: Agent --> %s'% (str(agentIndex), str(agentObj)))
    eval_csv_file.close()
    train_csv_writer.writerow(("-","-",str(agentObj)))
    train_csv_file.flush()
    train_csv_file.close()
    agentObj.quit()
    agentObj.finish_training()

if __name__ == '__main__':
    main()
