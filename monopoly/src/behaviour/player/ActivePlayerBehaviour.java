package behaviour.player;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import util.Logger;
import view.Case;
import view.CaseAchetable;
import agent.AgentJoueur;

public abstract class ActivePlayerBehaviour extends Behaviour{
	private static final long serialVersionUID = 1L;
	private ACLMessage msgReceived = null;

	protected abstract void decideAcquistareTerreno(CaseAchetable caseCourante);
	protected abstract void decideAcquistarecCasa();
	
	public ActivePlayerBehaviour(Agent a) {
		super(a);
	}
	
	@Override
	public void action(){
		msgReceived = myAgent.blockingReceive();
		if (msgReceived != null){
			switch (msgReceived.getPerformative()){
				/*
				 * Indique au joueur sur quelle case il se trouve après le déplacement effectué (dû au jeté de dés)
				 * Indica al giocatore quale casella è dopo la mossa (a causa del tiro dei dadi)
				 */
				case ACLMessage.INFORM_REF:
					
					try {
						CaseAchetable tribunale = null;
						((AgentJoueur)myAgent).setCaseCourante((Case) msgReceived.getContentObject());
						if (((AgentJoueur)myAgent).getCaseCourante() instanceof CaseAchetable){
							tribunale = (CaseAchetable) ((AgentJoueur)myAgent).getCaseCourante(); 
							if (tribunale.getProprietaireCase() == null){
								decideAcquistareTerreno(tribunale);
							}
						}
						decideAcquistarecCasa();
						
					}catch (UnreadableException e){e.printStackTrace();}
					
				break;
				
				//Recevoir de l'argent
				case ACLMessage.AGREE:
					int sommeRecue = Integer.parseInt(msgReceived.getContent().trim());
					((AgentJoueur)myAgent).setCapitalJoueur(((AgentJoueur)myAgent).getCapitalJoueur()+sommeRecue);
					Logger.info("TRANSIZIONE SOLDI : " + myAgent.getLocalName() + " -> +" + sommeRecue);
				break;
				
				//Payer une dette
				case ACLMessage.REQUEST:
					((AgentJoueur)myAgent).payerMontantDu(msgReceived);
				break;
				
				default: 
					System.err.println("Messaggio non gestito da ActivePlayerBehaviour" + myAgent.getLocalName() + " de " + msgReceived.getSender().getLocalName() + ":" + msgReceived.getPerformative());
				break;
			}
		}	
	}
	
	@Override
	public boolean done() {
		return msgReceived.getPerformative() == ACLMessage.INFORM_REF;
	}
}
