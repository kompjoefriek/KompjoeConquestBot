package bot;

import main.Region;
import move.AttackTransferMove;
import move.PlaceArmiesMove;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class BotParser
{
	final Scanner scan;

	final Bot bot;

	BotState currentState;

	public BotParser(Bot bot)
	{
		this.scan = new Scanner(System.in);
		this.bot = bot;
		this.currentState = new BotState();
	}


	public BotParser(Bot bot, String fileName )
	{
		File file = new File(fileName);
		Scanner scanner;
		try
		{
			scanner = new Scanner(file);
		}
		catch(Exception e)
		{
			scanner = new Scanner(System.in);
		}
		this.scan = scanner;
		this.bot = bot;
		this.currentState = new BotState();
	}


	public void run()
	{
		while (scan.hasNextLine())
		{
			String line = scan.nextLine().trim();
			if (line.length() == 0) { continue; }
			String[] parts = line.split(" ");
			if (parts[0].equals("pick_starting_regions"))
			{
				//pick which regions you want to start with
				currentState.setPickableStartingRegions(parts);
				ArrayList<Region> preferredStartingRegions = bot
					.getPreferredStartingRegions(currentState, Long.valueOf(parts[1]));
				String output = "";
				for (Region region : preferredStartingRegions)
					output = output.concat(region.getId() + " ");

				System.out.println(output);
			}
			else if (parts.length == 3 && parts[0].equals("go"))
			{
				//we need to do a move
				String output = "";
				if (parts[1].equals("place_armies"))
				{
					//place armies
					ArrayList<PlaceArmiesMove> placeArmiesMoves = bot
						.getPlaceArmiesMoves(currentState, Long.valueOf(parts[2]));
					for (PlaceArmiesMove move : placeArmiesMoves)
						output = output.concat(move.getString() + ",");
				}
				else if (parts[1].equals("attack/transfer"))
				{
					//attack/transfer
					ArrayList<AttackTransferMove> attackTransferMoves = bot
						.getAttackTransferMoves(currentState, Long.valueOf(parts[2]));
					for (AttackTransferMove move : attackTransferMoves)
						output = output.concat(move.getString() + ",");
				}
				if (output.length() > 0) System.out.println(output);
				else System.out.println("No moves");
			}
			else if (parts.length == 3 && parts[0].equals("settings"))
			{
				//update settings
				currentState.updateSettings(parts[1], parts[2]);
			}
			else if (parts[0].equals("setup_map"))
			{
				//initial full map is given
				currentState.setupMap(parts);
			}
			else if (parts[0].equals("update_map"))
			{
				//all visible regions are given
				currentState.updateMap(parts);
			}
			else if (parts[0].equals("opponent_moves"))
			{
				//all visible opponent moves are given
				currentState.readOpponentMoves(parts);
			}
			else if (parts[0].equals("debug_line"))
			{
				// Do nothing
			}
			else
			{
				System.err.printf("Unable to parse line \"%s\"\n", line);
			}
		}
	}
}
