package net.glowstone.entity;

import java.util.List;
import java.util.logging.Logger;
import net.glowstone.GlowChunk;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlockState;
import net.glowstone.io.StorageQueue;
import net.glowstone.io.WorldMetadataService;
import net.glowstone.net.Session;
import net.glowstone.util.PlayerListFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.plugin.SimplePluginManager;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;



/**
 *
 * @author hef
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Session.class, GlowServer.class, GlowWorld.class, EntityManager.class, SimplePluginManager.class, PlayerListFile.class, World.class, GlowChunk.class, Bukkit.class})
public class GlowPlayerTest {
    GlowPlayer glowPlayer;



    public GlowPlayerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {


    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        //mock objects
        Session mockSession = mock(Session.class);
        GlowServer mockGlowServer = mock(GlowServer.class);
        List<World> mockList = mock(List.class);
        GlowWorld mockGlowWorld = mock(GlowWorld.class);
        Logger mockLogger = mock(Logger.class);
        EntityManager mockEntityManager = mock(EntityManager.class);
        SimplePluginManager mockPluginManager = mock(SimplePluginManager.class);
        PlayerListFile mockPlayerListFile = mock(PlayerListFile.class);
        Environment mockEnvironment = mock(Environment.class);
        GlowChunk mockGlowChunk = mock(GlowChunk.class);
        Location mockLocation = mock(Location.class);
        WorldMetadataService mockWorldMetadataService = mock(WorldMetadataService.class);
        StorageQueue mockStorageQueue= mock(StorageQueue.class);
        mockStatic(Bukkit.class);

        //Session mocked methods
        when(mockSession.getServer()).thenReturn(mockGlowServer);
        when(mockSession.getState()).thenReturn(Session.State.GAME);

        //Server mocked methods
        when(mockGlowServer.getWorlds()).thenReturn(mockList);
        when(mockGlowServer.getLogger()).thenReturn(mockLogger);
        when(mockGlowServer.getPluginManager()).thenReturn(mockPluginManager);
        when(mockGlowServer.getOpsList()).thenReturn(mockPlayerListFile);
        when(mockGlowServer.getStorageQueue()).thenReturn(mockStorageQueue);

        //MockWorkd mocked methods
        when(mockGlowWorld.getEntityManager()).thenReturn(mockEntityManager);
        when(mockGlowWorld.getEnvironment()).thenReturn(mockEnvironment);
        when(mockGlowWorld.getChunkAt(anyInt(), anyInt())).thenReturn(mockGlowChunk);
        when(mockGlowWorld.getSpawnLocation()).thenReturn(mockLocation);
        when(mockGlowWorld.getMetadataService()).thenReturn(mockWorldMetadataService);

        //mockWorldList mocked methods
        when(mockList.get(0)).thenReturn(mockGlowWorld);

        //mockGlowChunk mocked methods
        when(mockGlowChunk.getTileEntities()).thenReturn(new GlowBlockState[0]);

        //Bukkit mocked methods
        when(Bukkit.getServer()).thenReturn(mockGlowServer);

        //Actual Setup
        glowPlayer = new GlowPlayer(mockSession,"Herobrine");
    }

    @After
    public void tearDown() {
        glowPlayer = null;
    }

    /**
     * Test of getExperience method, of class GlowPlayer.
     */
    @Test
    public void testGetExperience() {
        int expResult = 0;
        int result = glowPlayer.getExperience();
        assertEquals(expResult, result);
    }

    /**
     * Test of setExperience method, of class GlowPlayer.
     */
    @Test
    public void testSetExperience() {
        int exp = 0;
        int expResult = 0;
        glowPlayer.setExperience(exp);
        int result = glowPlayer.getExperience();
        assertEquals(expResult, result);
    }

    /**
     * Test of getLevel method, of class GlowPlayer.
     */
    @Test
    public void testGetLevel() {
        int expResult = 0;
        int result = glowPlayer.getLevel();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLevel method, of class GlowPlayer.
     */
    @Test
    public void testSetLevel() {
        int level = 0;
        int expLevel = 0;
        glowPlayer.setLevel(level);
        expLevel = glowPlayer.getLevel();
        assertEquals(level, expLevel);
    }

    /**
     * Test of getTotalExperience method, of class GlowPlayer.
     */
    @Test
    public void testGetTotalExperience() {
        int expResult = 0;
        int result = glowPlayer.getTotalExperience();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTotalExperience method, of class GlowPlayer.
     */
    @Test
    public void testSetTotalExperience() {
        int exp = 0;
        int result = 0;
        int expResult = 0;
        glowPlayer.setTotalExperience(exp);
        result = glowPlayer.getTotalExperience();
        assertEquals(expResult, result);
    }

    /**
     * Test of giveExp method, of class GlowPlayer.
     */
    @Test
    public void testGiveExp() {
        int xp = 0;
        int actualXp;
        int expectedXp = 0;
        glowPlayer.giveExp(xp);
        actualXp = glowPlayer.getTotalExperience();
        assertEquals(expectedXp, actualXp);
    }

    /**
     * Test of getExp method, of class GlowPlayer.
     */
    @Test
    public void testGetExp() {
        float expResult = 0.0F;
        float result = glowPlayer.getExp();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setExp method, of class GlowPlayer.
     */
    @Test
    public void testSetExp() {
        float percentToLevel = 0.0F;
        float result;
        glowPlayer.setExp(percentToLevel);
        result = glowPlayer.getExp();
        assertEquals(percentToLevel, result, 0.0);
    }

}
