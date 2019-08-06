package behaviour.player;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.Vector;

import view.CaseAchetable;
import agent.AgentJoueur;

/**
 * Comportement visant à bloquer les autres agents en visant les cases 
 * de couleur ayant déjà été achetés par des agents
 * Comportamento per bloccare altri agenti mediante caselle di targeting
  * colore già acquistato dagli agenti
 */
public class EvilBehaviour extends ActivePlayerBehaviour {
	private static final long serialVersionUID = 1L;
	private AgentJoueur agentJoueur;

	public EvilBehaviour(AgentJoueur agentJoueur) {
		super(agentJoueur);
		this.agentJoueur = agentJoueur;
		this.agentJoueur.setProbaDemandeLoyer(80);
	}

	public boolean canBlockPeople(CaseAchetable c) {
		Vector<AID> potentialAgents = c.getProprietairesPotentiels();
		//System.err.println("Proprios de la case: " + potentialAgents);
		if ( potentialAgents != null ) {
			if ( potentialAgents.size() == 0 ) {
				return false;
			}
			else {
				return true;
			}
		} 
		return false;
	}
	
	@Override
	protected void decideAcquistareTerreno(CaseAchetable caseCourante) {
		if (caseCourante.getProprietaireCase() == null){
			if(agentJoueur.getCapitalJoueur() > caseCourante.getValeurTerrain()){
				if( canBlockPeople(caseCourante)){
					ACLMessage demandeAchat = new ACLMessage(ACLMessage.SUBSCRIBE);
					demandeAchat.setContent(caseCourante.getPosition() + "");
					demandeAchat.addReceiver(agentJoueur.getMonopoly());
					agentJoueur.send(demandeAchat);
					System.out.println(agentJoueur.getLocalName() + " richiesta di acquisto " + caseCourante.getNom() );
					
					caseCourante.setProprietaireCase(agentJoueur.getAID());
					agentJoueur.addProprieteToJoueur(caseCourante);
				}
			}
			else
				System.out.println(agentJoueur.getLocalName() + " non ha abbastanza soldi per comprare\r\n"  + caseCourante.getNom());
		}
	}

	@Override
	protected void decideAcquistarecCasa() {
		
	}
}
