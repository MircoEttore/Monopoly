package behaviour.player;

import java.util.ArrayList;

import jade.lang.acl.ACLMessage;
import util.Logger;
import util.Constantes.Couleur;
import view.CaseAchetable;
import view.CaseTerrain;
import agent.AgentJoueur;

/**
 * Comportamento mirato ad acquisire terreni dello stesso colore (Qui 2 colori al massimo)
 */
public class CollectionneurBehaviour extends ActivePlayerBehaviour {
	private static final long serialVersionUID = 1L;
	private static Couleur colere1 = null;
	private static Couleur colore2 = null;
	private int virementEnAttente = 0;
	private AgentJoueur agentJoueur;
	
	public CollectionneurBehaviour(AgentJoueur agentJoueur) {
		super(agentJoueur);
		this.agentJoueur = agentJoueur;
		this.agentJoueur.setProbaDemandeLoyer(95);
	}

	@Override
	protected void decideAcquistareTerreno(CaseAchetable caseCourante) {
		if (caseCourante.getProprietaireCase() == null){
		
			int doitAcheter = 0;
			
			if(colere1 == null || colere1 == caseCourante.getCouleur())
				doitAcheter = 1;
			
			if(colore2 == null || colore2 == caseCourante.getCouleur())
				doitAcheter = 2;
			
			if(doitAcheter != 0){
				if(agentJoueur.getCapitalJoueur() > caseCourante.getValeurTerrain()){
					ACLMessage demandeAchat = new ACLMessage(ACLMessage.SUBSCRIBE);
					demandeAchat.setContent(caseCourante.getPosition() + "");
					demandeAchat.addReceiver(agentJoueur.getMonopoly());
					agentJoueur.send(demandeAchat);
					Logger.info(agentJoueur.getLocalName() + " richiesta di acquisto " + caseCourante.getNom());
					
					caseCourante.setProprietaireCase(agentJoueur.getAID());
					agentJoueur.addProprieteToJoueur(caseCourante);
					virementEnAttente = caseCourante.getValeurTerrain();
					
					if(doitAcheter == 1)
						colere1 = caseCourante.getCouleur();
					else
						colore2 = caseCourante.getCouleur();
				}
				else
					Logger.info(agentJoueur.getLocalName() + "Non ha abbstanza soldi per comprarla " + caseCourante.getNom());
			}
		}
	}

	@Override
	protected void decideAcquistarecCasa() {
ArrayList<CaseTerrain> cpp = agentJoueur.peutPoserMaisons();
		
		if(cpp.size() != 0){
			for(CaseTerrain ct : cpp){
				int prix = ct.getValeurMaison();
				
				if(agentJoueur.getCapitalJoueur() > (prix + virementEnAttente)){ //Il giocatore ha abbastanza soldi per comprare le case?
					ACLMessage demandeAchat = new ACLMessage(ACLMessage.PROXY);
					demandeAchat.setContent(ct.getPosition() + "#" + prix);
					demandeAchat.addReceiver(agentJoueur.getMonopoly());
					agentJoueur.send(demandeAchat);
					System.out.println(agentJoueur.getLocalName() + " Richiesta di acquisto della casa di " + ct.getNom());
					virementEnAttente += prix;
				}
				else
					System.out.println(agentJoueur.getLocalName() + " non ha abbastanza soldi per comprare case\r\n" +ct.getNom());
			}
		}
		else
			Logger.info(agentJoueur.getLocalName() + " ne peut pas encore acheter de maisons");
		
		virementEnAttente = 0;
	}
}
