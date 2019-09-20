package behaviour.player;

import jade.core.Agent;
import util.Logger;
import view.CaseAchetable;
import agent.AgentJoueur;

/**
 * Behaviour déclenché après tirage de dés
 *Non compra mai terreni
 */
public class StupideBehaviour extends ActivePlayerBehaviour {
	
	private static final long serialVersionUID = 1L;

	public StupideBehaviour(Agent agentJoueur) {
		super(agentJoueur);
		((AgentJoueur)myAgent).setProbaDemandeLoyer(45);
	}

	@Override
	protected void decideAcquistareTerreno(CaseAchetable caseCourante) {
		Logger.info(((AgentJoueur)myAgent).getLocalName() + " \r\n" + "è stupido: non compra terra o casa");
	}

	@Override
	protected void decideAcquistarecCasa() {
	}
}
