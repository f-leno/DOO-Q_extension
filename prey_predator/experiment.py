#!/usr/bin/env python
# encoding: utf-8

# Before running this program, first Start HFO server:
# $> ./bin/HFO --offense-agents 1

import argparse
import sys

import csv
import random
from environment import PredatorPreyEnvironment

#from cmac import CMAC


#from agents.agent import Agent





def get_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('-n','--number_agents',type=int, default=3)
    parser.add_argument('-p','--number_preys',type=int, default=1)
    parser.add_argument('-a','--agent',  default='Dummy') #Here, one agent class controls everything
    parser.add_argument('-t','--learning_trials',type=int, default=1500)
    parser.add_argument('-i','--evaluation_interval',type=int, default=5)
    parser.add_argument('-d','--evaluation_duration',type=int, default=100)
    parser.add_argument('-s','--seed',type=int, default=12345)
    parser.add_argument('-l','--log_file',default='/home/leno/gitProjects/DOO-Q_extension/log/')
    parser.add_argument('-et','--end_trials',type=int, default=1000)
    parser.add_argument('-it','--initial_trial',type=int, default=1)

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
    AGENT = AgentClass(seed=parameter.seed, numAg = parameter.number_agents)
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
    for trial in range(parameter.initial_trial,parameter.end_trials+1):
        print('***** %s: Start Trial' % str(trial))            
        random.seed(parameter.seed+trial)
        agent = build_agents()
        agents = [] 
   
        for i in range(parameter.number_agents):
            agents.append(agent.getAgRef(i))
        #The seed for initial state gneration must always be the same
        random.seed(parameter.seed)
        
        
        environment = PredatorPreyEnvironment(numberAgents = parameter.number_agents,agents = agents,
                                              preys=parameter.number_preys, evalEpisodes = parameter.evaluation_duration)    
        
        for i in range(parameter.number_agents):
             agents[i].connectEnv(environment,i)   
             
        random.seed(parameter.seed+trial)

                
        train_csv_writers = [None]*len(agents)
        train_csv_files = [None]*len(agents)
        eval_csv_writers = [None]*len(agents)
        eval_csv_files = [None]*len(agents)
        for i in range(len(agents)):
            logFolder = parameter.log_file + getattr(parameter,"agent")+"/_0_"+str(trial)+"_AGENT_"+str(i+1)+"_RESULTS"
            train_csv_files[i] = open(logFolder + "_train", "wb")
            train_csv_writers[i] = csv.writer(train_csv_files[i])
            train_csv_writers[i].writerow(("trial","steps_captured","used_budget"))
            train_csv_files[i].flush()
            eval_csv_files[i] = open(logFolder + "_eval", "wb")
            eval_csv_writers[i] = csv.writer(eval_csv_files[i])
            eval_csv_writers[i].writerow(("trial","steps_captured","used_budget"))
            eval_csv_files[i].flush()
    
        print("******* OK Output File Creation*********")
        
        
        
        
        #for agentIndex in range(len(agents)):
        #        agents[agentIndex].setup_advising(agentIndex,agents)
         
        #---Error prevention
        
                        
        # perform an evaluation trial
                
        for agentIndex in range(len(agents)):
            agents[agentIndex].set_exploring(False)
        stepsToCapture = 0

        for eval_trials in range(1,parameter.evaluation_duration+1):
            eval_step = 0
            environment.start_evaluation_episode()
            terminal = False
            #For all steps...
            limit = float('inf')
            while not terminal and eval_step <= limit:
                eval_step += 1
                state = [None]*len(agents)
                #Defines the action of each agent
                for agentIndex in range(len(agents)):
                    state[agentIndex],action = agents[agentIndex].action(agentIndex)
                #Process state transition
                environment.finish_state_transition()                        
                #Updates reward                            
                for agentIndex in range(len(agents)):
                    statePrime, action, reward = environment.step(agentIndex)                               
                    agents[agentIndex].observe_reward(state[agentIndex],action,statePrime,reward,agentIndex)                                          
                
                terminal = environment.is_terminal_state()
        #---        
        for episode in range(0,parameter.learning_trials+1):
                        

                # perform an evaluation trial
                if(episode % parameter.evaluation_interval == 0):
                    for agentIndex in range(len(agents)):
                        agents[agentIndex].set_exploring(False)
                    stepsToCapture = 0

                    for eval_trials in range(1,parameter.evaluation_duration+1):
                        eval_step = 0
                        environment.start_evaluation_episode()

                        terminal = False
                        #For all steps...
                        limit = float('inf')
                        while not terminal and eval_step <= limit:
                            eval_step += 1
                            #if episode>200:
                            #   print agents[0].state_importance(environment.get_state(0))
                            state = [None]*len(agents)
                            #Defines the action of each agent
                            for agentIndex in range(len(agents)):
                                state[agentIndex],action = agents[agentIndex].action(agentIndex)
                            #Process state transition
                            environment.finish_state_transition()                        
                            #Updates reward                            
                            for agentIndex in range(len(agents)):
                                    statePrime, action, reward = environment.step(agentIndex)                               
                                    agents[agentIndex].observe_reward(state[agentIndex],action,statePrime,reward,agentIndex)                                      
                            
                            terminal = environment.is_terminal_state()
            
                        stepsToCapture += eval_step
                    
                    stepsToCapture = float(stepsToCapture) / parameter.evaluation_duration
                    for agentIndex in range(len(agents)):
                        #if episode != 0:
                        eval_csv_writers[agentIndex].writerow((episode,"{:.2f}".format(stepsToCapture),str(agents[agentIndex].get_Q_size(agentIndex))))
                        eval_csv_files[agentIndex].flush()
                        agents[agentIndex].set_exploring(True)
                    print("*******Eval OK: "+str(episode)+" - Duration: "+str(stepsToCapture))
                 
                stepsToCapture = 0       
                eval_step = 0
                environment.start_learning_episode()
                terminal = False
                #For all steps...
                while not terminal:
                    eval_step += 1
                    state = [None]*len(agents)
                    #Defines the action of each agent
                    for agentIndex in range(len(agents)):
                        state[agentIndex],action = agents[agentIndex].action(agentIndex)
                    #Process state transition
                    environment.finish_state_transition()   
                    #Updates reward                            
                    for agentIndex in range(len(agents)):
                         statePrime, action, reward = environment.step(agentIndex)                               
                         agents[agentIndex].observe_reward(state[agentIndex],action,statePrime,reward,agentIndex) 
                    terminal = environment.is_terminal_state()
                    
                for agentIndex in range(len(agents)):
                    train_csv_writers[agentIndex].writerow((episode,"{:.2f}".format(eval_step),str(agents[agentIndex].get_Q_size(agentIndex))))
                    train_csv_files[agentIndex].flush()
                    
                    
        print('***** %s: END Trial' % str(trial)) 
            
    for agentIndex in range(len(agents)):
                    eval_csv_files[agentIndex].close()
                    train_csv_files[agentIndex].close()    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    #agentThreads = []
#    random.seed(parameter.seed)
#    #try:
#        #Initiating agent
#    agentRefs = [] 
#    agentThreads = []
#    for i in range(parameter.number_agents):
#            agentRefs.append(agent.getAgRef(i))
#            
#    for i in range(parameter.number_agents):
#        agentThreads.append(Thread(target = thread_agent, args=(agentRefs[i],agentRefs,i,parameter)))
#        agentThreads[i].start()
#        sleep(3)
#        
#    for i in range(parameter.number_agents):
#        agentThreads[i].join()
#            
#            
#    #except Exception as e:
#    #    print e.__doc__
#    #    print e.message
#    #    okThread = False
#   
#    
#
#    
#
#    
#    
#def thread_agent(agentObj,allAgents,agentIndex,mainParameters):
#    """This method is executed by each thread in the system and corresponds to the control
#    of one playing agent"""
#    
#    logFolder = mainParameters.log_file + getattr(mainParameters,"agent")+"/_0_"+str(mainParameters.number_trial)+"_AGENT_"+str(agentIndex+1)+"_RESULTS"
#    
#    #Connecting agent to server 
#    print "******Connecting agent "+str(agentIndex)+"****"
#    agentObj.connectHFO()
#    #Building Log folder name
#    print "******Connected agent "+str(agentIndex)+"****"
#    
#    print logFolder + "********" 
#    train_csv_file = open(logFolder + "_train", "wb")
#    train_csv_writer = csv.writer(train_csv_file)
#    train_csv_writer.writerow(("trial","frames_trial","goals_trial","used_budget"))
#    train_csv_file.flush()
#    print('***** %s: Setting up eval log files' % str(agentIndex))
#    #eval_csv_file = open(parameter.log_file + "_" + str(AGENT.unum) + "_eval", "wb")
#    eval_csv_file = open(logFolder + "_eval", "wb")
#    eval_csv_writer = csv.writer(eval_csv_file)
#    eval_csv_writer.writerow(("trial","goal_percentage","avg_goal_time","used_budget"))
#    eval_csv_file.flush()
#    
#    print('***** %s: Start training' % str(agentIndex))
#    for trial in range(mainParameter.initial_trial,mainParameters.learning_trials+1):
#        # perform an evaluation trial
#        if(trial % mainParameters.evaluation_interval == 0):
#            #print('***** %s: Running evaluation trials' % str(AGENT.unum) )
#            agentObj.set_exploring(False)
#            goals = 0.0
#            time_to_goal = 0.0
#            for eval_trials in range(1,mainParameters.evaluation_duration+1):
#                eval_frame = 0
#                eval_status = agentObj.IN_GAME
#                while eval_status == agentObj.IN_GAME:
#                    eval_frame += 1
#                    #print "****First Action****"
#                    agentObj.applyAction()
#                    #print "****First STEP****"
#                    eval_status, statePrime = agentObj.step()
#                    if(eval_status == agentObj.GOAL):
#                        goals += 1.0
#                        time_to_goal += eval_frame
#                        #print('********** %s: GGGGOOOOOOOOOOLLLL: %s in %s' % (str(AGENT.unum), str(goals), str(time_to_goal)))
#            goal_percentage = 100*goals/mainParameters.evaluation_duration
#            #print('***** %s: Goal Percentage: %s' % (str(AGENT.unum), str(goal_percentage)))
#            if (goals != 0):
#                avg_goal_time = time_to_goal/goals
#            else:
#                avg_goal_time = 0.0
#            #print('***** %s: Average Time to Goal: %s' % (str(AGENT.unum), str(avg_goal_time)))
#            # save stuff
#            #print "****Finished****"
#            eval_csv_writer.writerow((trial,"{:.2f}".format(goal_percentage),"{:.2f}".format(avg_goal_time),str(agentObj.get_Q_size())))
#            eval_csv_file.flush()
#            agentObj.set_exploring(True)
#            # reset agent trace
#            
#           
#        
#        #print('***** %s: Starting Learning Trial %d' % (str(AGENT.unum),trial))
#        status = agentObj.IN_GAME
#        frame = 0
#        while status == agentObj.IN_GAME:
#            frame += 1
#            state = agentObj.get_discretized_state()
#            #print "****First Action****"
#            action = agentObj.applyAction()
#            #print "****First STEP****"
#            status, statePrime = agentObj.step()
#            #print "****First REWARD****"
#            reward = agentObj.get_reward(status)
#            #print "****Observe REWARD****"
#            if reward==1: #positive reward
#                statePrime = ("GOAL")
#            agentObj.observe_reward(state,action,statePrime,reward)
#        #print('***** %s: Trial ended with %s'% (str(AGENT.unum), AGENT.hfo.statusToString(status)))
#        #print('***** %s: Agent --> %s'% (str(AGENT.unum), str(AGENT)))
#        #print "****Finished****"
#        # save stuff
#        train_csv_writer.writerow((trial,frame,reward,str(agentObj.get_Q_size())))
#        train_csv_file.flush()
#        #agentObj.stateActionTrace = {}
#
#
#
#        # Quit if the server goes down
#        if status == agentObj.SERVER_DOWN:
#            #agentObj.hfo.act(QUIT)
#            agentObj.quit()
#            print('***** %s: Shutting down agent' % str(agentIndex))
#            break
#    print('***** %s: Agent --> %s'% (str(agentIndex), str(agentObj)))
#    eval_csv_file.close()
#    train_csv_writer.writerow(("-","-",str(agentObj)))
#    train_csv_file.flush()
#    train_csv_file.close()
#    agentObj.quit()
#    agentObj.finish_training()

if __name__ == '__main__':
    main()
