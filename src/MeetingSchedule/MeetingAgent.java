package MeetingSchedule;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class MeetingAgent extends Agent {

    public ArrayList<Day> calendar ;
    //public ArrayList<MeetingAgent> contactList ;
    int id;
    private AID[] contacts;
    private DayName dayName;

    private MeetingAgentGUI myGui;

    protected void setup() {
        calendar = new ArrayList<>();
        calendar.add(new Day(DayName.MONDAY));

        System.out.println(getAID().getLocalName()+": Calendar created :\n"+calendar);

        myGui = new MeetingAgentGUI(this);
        myGui.display();

        System.out.println("Hello! " + getAID().getLocalName() + " is ready.");
        Object[] args = getArguments();
        if(args.length > 0) {
            id = Integer.valueOf((String)args[0]);
        }
        int interval = 3000;

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("meeting");
        sd.setName("JADE-meeting");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new ReceiveMeeting());
        addBehaviour(new UpdateMeeting());
        addBehaviour(new CancelMeeting());
        addBehaviour(new TickerBehaviour(this, interval)
        {
            protected void onTick()
            {
		  	/*
		  	// In case of timeout
		      if(timeout > 40000) {
                  System.out.println("Reached Time Out, cancelling order");
                  return;
              }*/
                //search only if the purchase task was ordered
                if (dayName != null)
                {
                    System.out.println("ORIGINAL:"+getAID().getLocalName() + ": Attempting meeting on " + dayName);
                    //update a list of known sellers (DF)
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("meeting");
                    template.addServices(sd);
                    try
                    {
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        System.out.println("ORIGINAL:"+getAID().getLocalName() + ": the following contacts have been found");
                        contacts = new AID[result.length-1];
                        int j = 0;
                        for (int i = 0; i < result.length; ++i)
                        {
                            if(!result[i].getName().getLocalName().equals(getAID().getLocalName())) {
                                contacts[j] = result[i].getName();
                                System.out.println(contacts[j].getLocalName());
                                j++;
                            }
                        }
                    }
                    catch (FIPAException fe)
                    {
                        fe.printStackTrace();
                    }

                    dayName = null;
                    myAgent.addBehaviour(new ProposeMeeting());
                    //timeout = System.currentTimeMillis() - start;
                }
            }
        });
    }

    public void lookForMeeting()
    {
        addBehaviour(new OneShotBehaviour()
        {
            public void action()
            {
                dayName = DayName.MONDAY;
                System.out.println("ORIGINAL:"+getAID().getLocalName() + ": Starting meeting negotiations for "+DayName.MONDAY);
            }
        });
    }



    private class ProposeMeeting extends Behaviour {

        private int repliesCnt = 0;
        private MessageTemplate mt;
        private int step = 0;
        private long start = 0;
        private long current = 0;
        boolean cancelled = false;
        private ArrayList<Day> calendars;
        private ArrayList<AID> received;
        private ArrayList<AID> received2;
        private Hour meeting;
        private HashMap<AID, Hour> temp;
        private DayName dayName;

        @Override
        public void action() {
            switch(step) {
                case 0:
                    dayName = DayName.MONDAY;
                    temp = new HashMap<>();
                    calendars = new ArrayList<>();
                    received = new ArrayList<>();
                    received2 = new ArrayList<>();
                    System.out.println("ORIGINAL:"+getAID().getLocalName() + ": starting negotiations.");
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < contacts.length; ++i) {
                        cfp.addReceiver(contacts[i]);
                    }

                    cfp.setContent(dayName.name());
                    cfp.setConversationId("meeting");
                    cfp.setReplyWith("cfp"+System.currentTimeMillis()); //unique value
                    myAgent.send(cfp);
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("meeting"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    start = System.currentTimeMillis();
                    step = 1;


                case 1:
                    //collect proposals
                    //System.out.println("ORIGINAL:"+"Starting case 1");
                    ACLMessage reply = myAgent.receive(mt);
                    if(current >= 4000) {
                        System.out.println("ORIGINAL:"+"Reaching case 2, timeout is : " + current);
                        System.out.println("ORIGINAL:"+"Timeout must have been reached and no reply was found. Continuing order.");
                        step = 2;
                        break;
                    }
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.PROPOSE && !received.contains(reply.getSender())) {
                            //proposal received
                            System.out.println("ORIGINAL:"+getAID().getLocalName()+": Received answer from "+reply.getSender().getLocalName());
                            received.add(reply.getSender());
                            try {
                                Day day= ((Day)reply.getContentObject());
                                calendars.add(day);
                            } catch (UnreadableException e) {
                                e.printStackTrace();
                            }
                            repliesCnt++;
                        }
                        if (repliesCnt >= contacts.length) {
                            //all proposals have been received
                            step = 2;
                        }
                    }
                    else {
                        System.out.println("ORIGINAL:"+getAID().getLocalName()+": blocked at case 1.");
                        //System.out.println(start);
                        current = System.currentTimeMillis() - start;
                        //System.out.println(current);
                        block(1000);
                    }
                    break;
                case 2:
                    System.out.println("Reaching case 2");
                    // Wait some time
                    try {
                        sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //received = new ArrayList<>();
                    if(calendars.size()==0) {
                        System.out.println("error");
                    }
                    Day res = calendar.get(0).merge(calendars);
                    meeting = res.getMeetingTime(false);
                    if(meeting == null) {
                        System.out.println("ORIGINAL:"+getAID().getLocalName() + ": no meeting hour was found.");

                        // For now, we simply take the highest weight even if it means some agents will have to be kicked
                        meeting = res.getMeetingTime(true);
                    }
                    else {
                        System.out.println("ORIGINAL:" + getAID().getLocalName() + ": Meeting hour was found:\n"+meeting);
                    }

                    ACLMessage order = new ACLMessage(ACLMessage.CONFIRM);
                    for (int i = 0; i < received.size(); ++i) {
                        order.addReceiver(received.get(i));
                    }
                    try {
                        order.setContentObject(meeting);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    order.setConversationId("meeting");
                    order.setReplyWith("meeting"+System.currentTimeMillis());
                    myAgent.send(order);
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("meeting"),
                            MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                    start = System.currentTimeMillis();
                    step = 3;
                    break;

                case 3:
                    //seller confirms the transaction
                    reply = myAgent.receive(mt);
                    if (reply != null) {
                        if (reply.getPerformative() == ACLMessage.INFORM && !received2.contains(reply.getSender())) {
                            received2.add(reply.getSender());
                            try {
                                temp.put(reply.getSender(), (Hour)reply.getContentObject());
                            } catch (UnreadableException e) {
                                e.printStackTrace();
                            }
                            if(received2.size() == received.size()) {
                                //purchase succeeded
                                if(calendar.get(0).isAvailable(meeting)) {
                                    calendar.get(0).lock(meeting);
                                    System.out.println("ORIGINAL:" + getAID().getLocalName() + ": Meeting was settled by all parties on " + dayName +", "+ meeting);
                                    System.out.println("ORIGINAL:" + getAID().getLocalName() + ": waiting for the next meeting order.");
                                    dayName = null;
                                    //myAgent.doDelete();
                                    System.out.println("ORIGINAL:" + getAID().getLocalName() + ": updated calendar:\n" + calendar);
                                    calendars = new ArrayList<>();
                                    step = 4;    //this state ends the purchase process
                                } else {
                                    cancelled = true; // the meeting of the OG was changed in the meantime
                                }
                            }
                        }
                        else {
                            System.out.println("ORIGINAL:"+getAID().getLocalName() + ": meeting was cancelled by "+ reply.getSender().getLocalName()+". Restarting negotiation process.");
                            cancelled = true;
                        }
                        if(cancelled) {
                            ACLMessage cancel = new ACLMessage(ACLMessage.CANCEL);
                            for (int i = 0; i < received2.size(); ++i) {
                                cancel.addReceiver(received2.get(i));
                                try {
                                    cancel.setContentObject(temp.get(received2.get(i)));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                cancel.setConversationId("meeting");
                                cancel.setReplyWith("meeting"+System.currentTimeMillis());
                                myAgent.send(cancel);
                            }

                            //mt = MessageTemplate.and(MessageTemplate.MatchConversationId("meeting"), MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                        }
                        if(current >= 6000) {
                            System.out.println("ORIGINAL:"+getAID().getLocalName() + ": Restarting negotiation.");
                            step = 0;
                        }
                    }
                    else {
                        System.out.println("ORIGINAL:"+"blocked at case 3.");
                        //System.out.println(start);
                        current = System.currentTimeMillis() - start;
                        //System.out.println(current);
                        block(3000);
                    }
                    break;
            }
        }

        @Override
        public boolean done() {
            //process terminates here if purchase has failed (title not on sale) or book was successfully bought
            return ((step == 2 && calendars.size() == 0) || step == 4);
        }
    }

    private class ReceiveMeeting extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if(msg != null) {
                System.out.println("RECEIVER:"+getAID().getLocalName() + ": Received meeting proposal from " + msg.getSender().getLocalName());
                ACLMessage reply = msg.createReply();
                if(calendar.get(0).wantToRefuse()) {
                    System.out.println("RECEIVER:"+getAID().getLocalName() + ": Wants to quit the meeting due to too low availability or no timetable interesting enough.");
                    reply.setPerformative(ACLMessage.REFUSE);
                } else {
                    reply.setPerformative(ACLMessage.PROPOSE);
                }
                try {
                    reply.setContentObject(calendar.get(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myAgent.send(reply);
            }
        }
    }

    private class UpdateMeeting extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
            ACLMessage msg = myAgent.receive(mt);
            if(msg != null) {
                Hour meeting = null;
                System.out.println("RECEIVER:"+getAID().getLocalName() + ": Received meeting hour from " + msg.getSender().getLocalName());
                try {
                    meeting = (Hour) msg.getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                ACLMessage reply = msg.createReply();
                if(calendar.get(0).isAvailable(meeting)) {
                    System.out.println("RECEIVER:"+getAID().getLocalName() + ": Meeting hour is free. Updating.");
                    Hour old = calendar.get(0).lock(meeting);

                    reply.setPerformative(ACLMessage.INFORM);
                    try {
                        reply.setContentObject(old);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("RECEIVER:"+getAID().getLocalName() + ": Meeting hour is unavailable. Cancelling meeting.");

                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                }
                myAgent.send(reply);
            }
        }
    }

    private class CancelMeeting extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CANCEL);
            ACLMessage msg = myAgent.receive(mt);
            if(msg != null) {
                Hour old = null;
                System.out.println("RECEIVER:"+getAID().getLocalName() + ": Meeting was cancelled, reverting to old. ");

                try {
                    old = (Hour)msg.getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                calendar.get(0).lock(old);
            }
        }
    }

}