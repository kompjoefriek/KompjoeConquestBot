package main;

import java.util.LinkedList;

public class SuperRegion
{
	public static final int MIN_GUARD_BORDER_REGION = 1; // Use this to enable guarding
	public static final int MIN_GUARD_REGION = 1;

	private int m_id;
	private int m_armiesReward;
	private LinkedList<Region> m_subRegions;
	private LinkedList<Region> m_borderRegions;
	private boolean m_fullyGuarded;

	public SuperRegion(int id, int armiesReward)
	{
		m_id = id;
		m_armiesReward = armiesReward;
		m_subRegions = new LinkedList<Region>();
		m_borderRegions = new LinkedList<Region>();
		m_fullyGuarded = false;
	}

	public void addSubRegion(Region subRegion)
	{
		if (!m_subRegions.contains(subRegion)) { m_subRegions.add(subRegion); }
	}

	/**
	 * @return A string with the name of the player that fully owns this SuperRegion
	 */
	public String ownedByPlayer()
	{
		String playerName = m_subRegions.getFirst().getPlayerName();
		for (Region region : m_subRegions)
		{
			if (!playerName.equals(region.getPlayerName())) { return null; }
		}
		return playerName;
	}

	/**
	 * @return The id of this SuperRegion
	 */
	public int getId()
	{
		return m_id;
	}

	/**
	 * @return The number of armies a Player is rewarded when he fully owns this SuperRegion
	 */
	public int getArmiesReward()
	{
		return m_armiesReward;
	}

	/**
	 * @return A list with the Regions that are part of this SuperRegion
	 */
	public LinkedList<Region> getSubRegions()
	{
		return m_subRegions;
	}

	public void updateFullyGuarded()
	{
		m_fullyGuarded = false;
		if (ownedByPlayer() != null)
		{
			// All sub regions are owned by one player
			m_fullyGuarded = true;
			for (Region region : m_subRegions)
			{
				if (region.getNeighborSuperRegions().size() > 0)
				{
					if (region.getArmies() < MIN_GUARD_BORDER_REGION) { m_fullyGuarded = false; }
				}
				else
				{
					if (region.getArmies() < MIN_GUARD_REGION) { m_fullyGuarded = false; }
				}
			}
		}
	}

	public boolean getFullyGuarded()
	{
		return m_fullyGuarded;
	}

	/**
	 * @return A list with the Regions that are part of this SuperRegion, and connect to another SuperRegion
	 */
	public LinkedList<Region> getBorderRegions()
	{
		return m_borderRegions;
	}


	public void addBorderRegion(Region region)
	{
		if (!m_borderRegions.contains(region)) { m_borderRegions.add(region); }
	}


	public int comparePreferredTo(SuperRegion compareSuperRegion)
	{
		// No reward? DO NO WANT (http://weknowmemes.com/wp-content/uploads/2011/11/do-not-want-guy.jpg)
		if (getArmiesReward() == 0) { return -1; }
		if (compareSuperRegion.getArmiesReward() == 0) { return 1; }

		int totalArmies = 0;
		for (Region region : getSubRegions())
		{
			if (region.getArmies() > 2)
			{
				totalArmies += region.getArmies() * 2;
			}
			else
			{
				totalArmies += region.getArmies();
			}
		}
		int totalCompareArmies = 0;
		for (Region region : compareSuperRegion.getSubRegions())
		{
			if (region.getArmies() > 2)
			{
				totalCompareArmies += region.getArmies() * 2;
			}
			else
			{
				totalCompareArmies += region.getArmies();
			}
		}
		if (totalArmies > 0 && totalCompareArmies > 0)
		{
			double superValue = getArmiesReward() / (double)totalArmies;
			double compareSuperValue = compareSuperRegion.getArmiesReward() / (double)totalCompareArmies;
			if (superValue > compareSuperValue)
			{
				return -1;
			}
			if (superValue < compareSuperValue)
			{
				return 1;
			}
		}

		// Smaller super regions are preferred
		//if (getSubRegions().size() != compareSuperRegion.getSubRegions().size())
		//{
		//	return getSubRegions().size() - compareSuperRegion.getSubRegions().size();
		//}
		// More award is preferred, so i turned them around here :-)
		if (compareSuperRegion.getArmiesReward() != getArmiesReward())
		{
			return compareSuperRegion.getArmiesReward() - getArmiesReward();
		}

		// Less border regions are preferred
		return getBorderRegions().size() - compareSuperRegion.getBorderRegions().size();

		// Less border regions are preferred
		//if (getBorderRegions().size() != compareSuperRegion.getBorderRegions().size())
		//{
		//	return getBorderRegions().size() - compareSuperRegion.getBorderRegions().size();
		//}

		// More award is preferred, so i turned them around here :-)
		//return compareSuperRegion.getArmiesReward() - getArmiesReward();
	}


	public String toString()
	{
		return "id:" +  m_id + "," +
			"name:" + SuperRegionName.getName(m_id) + "," +
			"armiesRewarded:" + m_armiesReward + "," +
			"fullyGuarded:" +  m_fullyGuarded;
	}
}
