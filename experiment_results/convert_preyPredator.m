function convert_preyPredator(path,repetitions,numberAgents)
%This function aims at converting the standard output of the Python
%Predator-Prey codification to one similar to the default one in the
%gridworld domain.
%The results must be stored in a prey-predator_original folder in the same
%path as this file, which will be converted inside a pre-predator folder.
%
%Parameters:
% path: path of the result folder
% repetitions: Number of repetitions of the experiment
% numberAgents: Number of agents in the experiment

algorithms = { 'DOOQ', 'MAQL','SAQL','DQL','Dummy'};

for alg = algorithms
    for rep = 1:repetitions
        outputData = zeros(0,5); %epis,Qtable,steps,cumReward,discountedRew
        for agent = 1:numberAgents
            %Read original file
            fileName = char(strcat(path,'/prey-predator_original/', alg, '/', '_0_' , num2str(rep),'_AGENT_',num2str(agent),'_RESULTS_eval'));
            fileContent = csvread(fileName,1);
            %Fixing size of matrix
            if size(fileContent,1) ~= size(outputData,1)
               outputData(1:size(fileContent,1),:) = zeros(size(fileContent,1), 5);
            end
            outputData(:,1) = fileContent(:,1); %epis
            outputData(:,2) = outputData(:,2) + fileContent(:,3); %qtable
            outputData(:,3) = fileContent(:,2); %steps
        end
        outputData(:,2) = outputData(:,2)/numberAgents;
            
        
        %Write in new format
        outputFile = char(strcat(path,'/prey-predator/', alg, '/', 'result' , num2str(rep),'.csv'));
        cHeader = {'epis'	'QTable'	'steps'	'cumReward'	'discountedRew'}; %header
        commaHeader = [cHeader;repmat({';'},1,numel(cHeader))]; %insert commaas
        commaHeader = commaHeader(:)';
        textHeader = cell2mat(commaHeader); %cHeader in text with commas
        
        %Create folder
        [filepath,name,ext] = fileparts(outputFile);
        if ~exist(filepath,'dir')
            mkdir(filepath);
        end
        
        %write header to file
        fid = fopen(outputFile,'w'); 
        fprintf(fid,'%s\n',textHeader);
        
        
        %write data to end of file
        for i = 1:size(outputData,1)
            fprintf(fid,'%s\n',char([num2str(outputData(i,1)),';',num2str(outputData(i,2)),';',num2str(outputData(i,3)),';',num2str(outputData(i,4)),';',num2str(outputData(i,5)),';']));
        end
        %dlmwrite(outputFile,outputData,'delimiter',';','-append');
        fclose(fid);
    end
end

end