package com.massivecraft.factions.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.massivecraft.factions.comparator.ComparatorMPlayerRole;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsFactionShowAsync;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.PriorityLines;
import com.massivecraft.massivecore.util.TimeDiffUtil;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;

public class EngineShow extends Engine
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public static final String BASENAME_ = "factions_";
	
	public static final String SHOW_ID_FACTION_ID = BASENAME_ + "id";
	public static final String SHOW_ID_FACTION_DESCRIPTION = BASENAME_ + "description";
	public static final String SHOW_ID_FACTION_AGE = BASENAME_ + "age";
	public static final String SHOW_ID_FACTION_POWER = BASENAME_ + "power";
	public static final String SHOW_ID_FACTION_TOP = BASENAME_ + "top";
	public static final String SHOW_ID_FACTION_STATS = BASENAME_ + "stats";
	public static final String SHOW_ID_FACTION_FOLLOWERS = BASENAME_ + "followers";
	public static final String SHOW_ID_FACTION_ALIADOS = BASENAME_ + "allys";
	public static final String SHOW_ID_FACTION_INIMIGOS = BASENAME_ + "enemies";
	
	public static final int SHOW_PRIORITY_FACTION_ID = 1000;
	public static final int SHOW_PRIORITY_FACTION_DESCRIPTION = 2000;
	public static final int SHOW_PRIORITY_FACTION_AGE = 3000;
	public static final int SHOW_PRIORITY_FACTION_POWER = 4000;
	public static final int SHOW_PRIORITY_FACTION_TOP = 5000;
	public static final int SHOW_PRIORITY_FACTION_STATS = 6000;
	public static final int SHOW_PRIORITY_FACTION_FOLLOWERS = 9000;
	public static final int SHOW_PRIORITY_FACTION_ALIADOS = 10000;
	public static final int SHOW_PRIORITY_FACTION_INIMIGOS = 11000;
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineShow i = new EngineShow();
	public static EngineShow get() { return i; }

	// -------------------------------------------- //
	// FACTION SHOW
	// -------------------------------------------- //

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onFactionShow(EventFactionsFactionShowAsync event)
	{
		final int tableCols = 4;
		final CommandSender sender = event.getSender();
		final MPlayer mplayer = event.getMPlayer();
		final Faction faction = event.getFaction();
		final boolean normal = faction.isNormal();
		final Map<String, PriorityLines> idPriorityLiness = event.getIdPriorityLiness();
		String none = Txt.parse("�7�oNingu�m");

		// ID
		if (mplayer.isOverriding())
		{
			show(idPriorityLiness, SHOW_ID_FACTION_ID, SHOW_PRIORITY_FACTION_ID, "ID", faction.getId());
		}

		// DESCRIPTION
		show(idPriorityLiness, SHOW_ID_FACTION_DESCRIPTION, SHOW_PRIORITY_FACTION_DESCRIPTION, "Descri��o", faction.getDescriptionDesc());
		
		// SECTION: NORMAL
		if (normal)
		{
			// AGE
			long ageMillis = faction.getCreatedAtMillis() - System.currentTimeMillis();
			LinkedHashMap<TimeUnit, Long> ageUnitcounts = TimeDiffUtil.limit(TimeDiffUtil.unitcounts(ageMillis, TimeUnit.getAllButMillis()), 3);
			String ageDesc = TimeDiffUtil.formatedMinimal(ageUnitcounts, "�e");
			show(idPriorityLiness, SHOW_ID_FACTION_AGE, SHOW_PRIORITY_FACTION_AGE, "�6Criada h�", ageDesc + "�e atr�s");

			// POWER
			String powerDesc = Txt.parse("%d/%d/%d", faction.getLandCount(), faction.getPowerRounded(), faction.getPowerMaxRounded());
			show(idPriorityLiness, SHOW_ID_FACTION_POWER, SHOW_PRIORITY_FACTION_POWER, "Terras / Poder / Poder M�ximo", powerDesc);

			// TOP
			String topDesc = Txt.parse("%d��e Lugar", faction.getTopPosition());
			show(idPriorityLiness, SHOW_ID_FACTION_TOP, SHOW_PRIORITY_FACTION_TOP, "Posi��o no Ranking", topDesc);
			
			// STATS
			String kdr = faction.getKdrRounded();
			int kills = faction.getKills();
			int deaths = faction.getDeaths();
			String statsDesc = Txt.parse("%d/%d/%s", kills, deaths,kdr);
			show(idPriorityLiness, SHOW_ID_FACTION_STATS, SHOW_PRIORITY_FACTION_STATS, "Abates / Mortes / Kdr", statsDesc);
			
			// ALLYS and ENEMIES
			String aliados = "";
			String rival = "";
			String aliadosColor = MConf.get().colorAlly.toString();
			String rivalColor = MConf.get().colorEnemy.toString();
			
			for (Faction f : faction.getAllys()) {
				aliados += "�8, " + aliadosColor + f.getName();
			}
			
			for (Faction f : faction.getEnemies()) {
				rival +=  "�8, " + rivalColor + f.getName();
			}
				
			if (aliados.equals("")) {
				aliados = "....�7�oNenhum";
			}
			if (rival.equals("")) {
				rival = "....�7�oNenhum";
			}
			
			show(idPriorityLiness, SHOW_ID_FACTION_ALIADOS, SHOW_PRIORITY_FACTION_ALIADOS, "Aliados", aliados.substring(4,aliados.length()));
			show(idPriorityLiness, SHOW_ID_FACTION_INIMIGOS, SHOW_PRIORITY_FACTION_INIMIGOS, "Inimigos", rival.substring(4,rival.length()));
		}

		// FOLLOWERS
		List<String> followerLines = new ArrayList<>();

		List<String> followerNamesOnline = new ArrayList<>();
		List<String> followerNamesOffline = new ArrayList<>();

		List<MPlayer> followers = faction.getMPlayers();
		Collections.sort(followers, ComparatorMPlayerRole.get());
		for (MPlayer follower : followers)
		{
			if (follower.isOnline(sender))
			{
				if (normal) {
				followerNamesOnline.add("�f" + follower.getRole().getPrefix() + "�f" + follower.getDisplayName(mplayer));
				} else {
				followerNamesOnline.add("�f" + follower.getDisplayName(mplayer));
				}
			}
			else if (normal)
			{
				// For the non-faction we skip the offline members since they are far to many (infinite almost)
				followerNamesOffline.add(follower.getRole().getPrefix() + follower.getDisplayName(mplayer));
			}
		}
		
		String headerOnline = Txt.parse("�6Membros Online (%s):", followerNamesOnline.size());
		followerLines.add(headerOnline);
		if (followerNamesOnline.isEmpty())
		{
			followerLines.add(none);
		}
		else
		{
			followerLines.addAll(table(followerNamesOnline, tableCols));
		}

		if (normal)
		{
			String headerOffline = Txt.parse("�6Membros Offline (%s):", followerNamesOffline.size());
			followerLines.add(headerOffline);
			if (followerNamesOffline.isEmpty())
			{
				followerLines.add(none);
			}
			else
			{
				followerLines.addAll(table(followerNamesOffline, tableCols));
			}
		}
		idPriorityLiness.put(SHOW_ID_FACTION_FOLLOWERS, new PriorityLines(SHOW_PRIORITY_FACTION_FOLLOWERS, followerLines));
	}

	public String show(String key, String value)
	{
		return Txt.parse("�6%s: �e%s", key, value);
	}

	public PriorityLines show(int priority, String key, String value)
	{
		return new PriorityLines(priority, show(key, value));
	}

	public void show(Map<String, PriorityLines> idPriorityLiness, String id, int priority, String key, String value)
	{
		idPriorityLiness.put(id, show(priority, key, value));
	}

	public List<String> table(List<String> strings, int cols)
	{
		List<String> ret = new ArrayList<>();

		StringBuilder row = new StringBuilder();
		int count = 0;

		Iterator<String> iter = strings.iterator();
		while (iter.hasNext())
		{
			String string = iter.next();
			row.append(string);
			count++;

			if (iter.hasNext() && count != cols)
			{
				row.append(Txt.parse(" �e| "));
			}
			else
			{
				ret.add(row.toString());
				row = new StringBuilder();
				count = 0;
			}
		}

		return ret;
	}

}
