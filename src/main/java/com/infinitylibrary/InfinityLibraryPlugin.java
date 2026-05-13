package com.infinitylibrary;

import com.infinitylibrary.command.CommandHandler;
import com.infinitylibrary.database.MySqlManager;
import com.infinitylibrary.engine.GenerationEngine;
import com.infinitylibrary.economy.EconomyManager;
import com.infinitylibrary.gui.GUIManager;
import com.infinitylibrary.listener.BookListener;
import com.infinitylibrary.listener.GUIListener;
import com.infinitylibrary.listener.NewspaperListener;
import com.infinitylibrary.listener.PlayerGenerationListener;
import com.infinitylibrary.navigation.RouteRegistry;
import com.infinitylibrary.newspaper.NewspaperManager;
import com.infinitylibrary.placeholder.LibraryPlaceholderExpansion;
import com.infinitylibrary.recommendation.RecommendationEngine;
import com.infinitylibrary.rental.RentalManager;
import com.infinitylibrary.scoreboard.LibraryScoreboardManager;
import com.infinitylibrary.room.RoomManager;
import com.infinitylibrary.selection.SelectionManager;
import com.infinitylibrary.storage.BookStorageManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class InfinityLibraryPlugin extends JavaPlugin {
    private RoomManager roomManager;
    private GenerationEngine generationEngine;
    private BookStorageManager bookStorageManager;
    private GUIManager guiManager;
    private SelectionManager selectionManager;
    private MySqlManager mySqlManager;
    private NewspaperManager newspaperManager;
    private RecommendationEngine recommendationEngine;
    private RouteRegistry routeRegistry;
    private RentalManager rentalManager;
    private EconomyManager economyManager;
    private LibraryScoreboardManager scoreboardManager;

    @Override public void onEnable() {
        saveDefaultConfig();
        roomManager = new RoomManager(this); roomManager.load();
        bookStorageManager = new BookStorageManager(this); bookStorageManager.load();
        selectionManager = new SelectionManager(this);
        mySqlManager = new MySqlManager(this); mySqlManager.connect();
        newspaperManager = new NewspaperManager(this);
        recommendationEngine = new RecommendationEngine(bookStorageManager);
        routeRegistry = new RouteRegistry();
        rentalManager = new RentalManager(this); rentalManager.load();
        economyManager = new EconomyManager(this); economyManager.hook();
        scoreboardManager = new LibraryScoreboardManager(this); scoreboardManager.start();
        guiManager = new GUIManager(this);
        generationEngine = new GenerationEngine(this, roomManager); generationEngine.start();
        CommandHandler handler = new CommandHandler(this);
        PluginCommand command = getCommand("infinitylibrary");
        if (command != null) { command.setExecutor(handler); command.setTabCompleter(handler); }
        getServer().getPluginManager().registerEvents(new PlayerGenerationListener(this), this);
        getServer().getPluginManager().registerEvents(new BookListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new NewspaperListener(this), this);
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) new LibraryPlaceholderExpansion(this).register();
        getLogger().info("InfinityLibraryPlugin enabled.");
    }

    @Override public void onDisable() {
        if (generationEngine != null) generationEngine.stop();
        if (bookStorageManager != null) bookStorageManager.saveNow();
        if (rentalManager != null) rentalManager.save();
        if (scoreboardManager != null) scoreboardManager.stop();
        if (mySqlManager != null) mySqlManager.close();
    }

    public void reloadEverything() {
        reloadConfig();
        roomManager.load();
        bookStorageManager.load();
        generationEngine.stop();
        generationEngine.start();
    }

    public RoomManager getRoomManager() { return roomManager; }
    public GenerationEngine getGenerationEngine() { return generationEngine; }
    public BookStorageManager getBookStorageManager() { return bookStorageManager; }
    public GUIManager getGuiManager() { return guiManager; }
    public SelectionManager getSelectionManager() { return selectionManager; }
    public MySqlManager getMySqlManager() { return mySqlManager; }
    public NewspaperManager getNewspaperManager() { return newspaperManager; }
    public RecommendationEngine getRecommendationEngine() { return recommendationEngine; }
    public RouteRegistry getRouteRegistry() { return routeRegistry; }
    public RentalManager getRentalManager() { return rentalManager; }
    public EconomyManager getEconomyManager() { return economyManager; }
    public LibraryScoreboardManager getScoreboardManager() { return scoreboardManager; }
}
