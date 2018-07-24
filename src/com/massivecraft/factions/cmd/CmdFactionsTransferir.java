package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;

public class CmdFactionsTransferir extends FactionsCommand {

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdFactionsTransferir() {
		
		// Aliases
		this.addAliases("lider", "lideran�a", "lideranca");
		
		// Parametros (necessario)
		this.addParameter(TypeMPlayer.get(), "jogador");
		
		// Requisi��es
		this.addRequirements(ReqHasFaction.get());
		
		// Descri��o do comando
		this.setDesc("�6 transferir �e<player> �8-�7 Transfere a lideran�a da fac��o.");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void perform() throws MassiveException {
		MPlayer target = this.readArg(msender);
		Faction facSender = msender.getFaction();
		Faction facTarget = target.getFaction();
		Rel cargoSender = msender.getRole();
		
		// Verificando se o sender � lider da fac��o
		if (cargoSender != Rel.LEADER) {
			msender.message("�cApenas o l�der da fac��o pode promover um capit�o para l�der.");
			return;
		}
		
		// Verificando se o sender e o target s�o a mesma pessoa
		if (msender == target) {
			msender.message("�cVoc� n�o pode transferir a lideran�a para voc� mesmo");
			return;
		}

		// Verificando se o target � da mesma fac�o que o sender
		if (facSender != facTarget) {
			msender.message("�cEste jogador n�o esta na sua fac��o.");
			return;
		}
		
		// Aplicando o evento
		msender.setRole(Rel.OFFICER);
		target.setRole(Rel.LEADER);
		
		// Informando o sender e o target
		facSender.msg("�e" + msender.getName() + "�e transferiu a lidera��o da fac��o para \"�e" + target.getName() + "\"�e.");
	}
}
