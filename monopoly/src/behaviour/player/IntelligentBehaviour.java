package behaviour.player;

import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Vector;

import util.Logger;
import view.CaseAchetable;
import view.CaseTerrain;
import agent.AgentJoueur;

/**
 * Comportement visant à acheter des terrains en fonction des prix que lui apportent les terrains
 * Comportamento all'acquisto di terreni in base ai prezzi del terreno
 */
public class IntelligentBehaviour extends ActivePlayerBehaviour {
	private static final long serialVersionUID = 1L;
	
	private static final int SOGLIA_DI_INTERESSE_DI_ACQUISTO = 3000;
	private static final int SOGLIA_DI_ACQUISTO_CASE = 60000;
	private static final int SOGLIA_DI_ACQUISTO_TERRENI = 40000;
	private int virementEnAttente = 0;
	
	private AgentJoueur agentJoueur;
	

	public IntelligentBehaviour(AgentJoueur myAgent) {
		super(myAgent);
		this.agentJoueur = myAgent;
		this.agentJoueur.setProbaDemandeLoyer(90);
	}
	
	public boolean isInteressantLoyer(Vector<Integer> loyers) {
		if ( loyers.get(0) * 2 < loyers.get(1) ) {
			return true;
		}
		return loyers.get(0) > SOGLIA_DI_INTERESSE_DI_ACQUISTO;
	}


	@Override
	protected void decideAcquistareTerreno(CaseAchetable caseCourante) {
		if (caseCourante.getProprietaireCase() == null){
			if(agentJoueur.getCapitalJoueur() > caseCourante.getValeurTerrain() && agentJoueur.getCapitalJoueur() > SOGLIA_DI_ACQUISTO_TERRENI){
				Vector<Integer> loyers = caseCourante.getLoyers();
				if( isInteressantLoyer(loyers) ){
					ACLMessage demandeAchat = new ACLMessage(ACLMessage.SUBSCRIBE);
					demandeAchat.setContent(caseCourante.getPosition() + "");
					demandeAchat.addReceiver(agentJoueur.getMonopoly());
					agentJoueur.send(demandeAchat);
					Logger.info(agentJoueur.getLocalName() + " demande a acheter " + caseCourante.getNom());
					
					caseCourante.setProprietaireCase(agentJoueur.getAID());
					agentJoueur.addProprieteToJoueur(caseCourante);
					virementEnAttente = caseCourante.getValeurTerrain();
				}
			}
			else
				Logger.info(agentJoueur.getLocalName() + " n'a pas assez d'argent pour acheter "+ caseCourante.getNom());
		}
	}

	@Override
	protected void decideAcquistarecCasa() {
ArrayList<CaseTerrain> cpp = agentJoueur.peutPoserMaisons();
		
		if(cpp.size() != 0){
			for(CaseTerrain ct : cpp){
				int prix = ct.getValeurMaison();
				
				if((agentJoueur.getCapitalJoueur() > (prix + virementEnAttente)) && (agentJoueur.getCapitalJoueur() > (SOGLIA_DI_ACQUISTO_CASE + virementEnAttente))){ //Il giocatore ha abbastanza soldi per comprare le case?
					ACLMessage demandeAchat = new ACLMessage(ACLMessage.PROXY);
					demandeAchat.setContent(ct.getPosition() + "#" + prix);
					demandeAchat.addReceiver(agentJoueur.getMonopoly());
					agentJoueur.send(demandeAchat);
					System.out.println(agentJoueur.getLocalName() + " demande a acheter des maisons sur " + ct.getNom());
					virementEnAttente += prix;
				}
				else
					System.out.println(agentJoueur.getLocalName() + " n'a pas assez d'argent pour acheter des maisons sur " + ct.getNom());
			}
		}
		else
			Logger.info(agentJoueur.getLocalName() + " ne peut pas encore acheter de maisons");
		
		virementEnAttente = 0;
	}
}
