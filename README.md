

# JADE PROJECT
# Multi-agent meeting scheduling
 

# Project overview
The problem concerns planning a meeting among several participants. We know from experience that setting up a meeting with more than 3-4 independent participants can be a tough job. This can be viewed as a constraint satisfaction problem, where each participant has time constraints resulting from their calendar and preferences. There are numerous approaches to coping with this question. The problem is decentralized in nature, usually there is no arbitrary party that can facilitate and manipulate the process of negotiation. This can be tackled by a multi-agent system.

 

# Definition of the problem

There are N agents among whom a meeting is to be set up. Each agent has:

contact list for participants selection (the contact list is a subset of the set of all agents on the platform which can be invited),
its calendar (CAL) of a certain size which is a set consisting of time slots when meetings could be scheduled, assume that CAL is finite (=> bounded and enumerable), slot starts at full time and lasts for the multiplicity of 1 hour,
preferences expressed by a function f: CAL -> [0,1], the higher the value of f, the more preferred the slot is for a meeting for the particular agent,
(may have) some meetings already planned which means that some slots are unavailable (we define a meeting as an unavailable slot), we assume that meetings planned cannot be cancelled (agent in the process of scheduling a new meeting cannot cancel or reschedule other meetings to retrieve a time slot),
Agent's calendar is a private space, held by the agent and not known to the other agents. Agents share only the information passed during the negotiation process which is necessary to set up a meeting.

Basically, an agent reveals to the others only the information that the agent wants to reveal, using standard ACL message passing. There is no shared space for computation nor special agents facilitating the process who would not be the intended participants of the meeting.

# The construction of a protocol should:

ensure that the meeting is always scheduled if there exists at least one available slot for all agents under consideration,
maximize the global preference (=sum of preferences of all agents for a given slot),
not eliminate any invited agent from the process unless the agent expresses it explicitly,
take into account that the situation may change during the process of scheduling (the slot under consideration may be taken by another process or the preferences may change).
 

# Project objective
The goal is to design and implement a protocol for meeting scheduling that would satisfy 1-4. The protocol may take several steps to complete, each agent may send more than one proposal at once - it is all up to you. The intention is to imitate a natural process of meeting scheduling in a decentralized fashion. Running your agents on different machines (PCs, smartphones or any other Java-enabled device) will be a plus.

# Follow the guidelines:
1. identify agent types.
2. identify agent responsibilities,
3. set the naming scheme,
4. identify the interaction between agents,
5. design the basic algorithm for the protocol,
6. establish the content language, basic concepts and templates for message exchange,
7. design agent behaviours based on identified interaction, algorithm and communication elements,
8. implement the multi-agent system on the JADE platform.

The scheduling process should be readable and understandable. Please, print out all the necessary information to visualize your process. There is no need for UI elements, you can pass all your input data as program parameters.

 
