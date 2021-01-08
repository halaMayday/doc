package com.mlcloud.fusioncloud.service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.mlcloud.fusioncloud.FusinonCloudAccess;
import com.mlcloud.fusioncloud.bean.DetailVolumeResponse;
import com.mlcloud.fusioncloud.bean.InstanceModel;
import com.mlcloud.fusioncloud.bean.OpenRcBean;
import com.mlcloud.fusioncloud.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openstack4j.model.common.BasicResource;
import org.openstack4j.model.common.IdEntity;
import org.openstack4j.model.compute.SecurityGroup;
import org.openstack4j.model.network.State;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：hf
 * @date ：Created in 2020/12/18 1:45 下午
 */
@Slf4j
@Ignore
public class FusioncloudCloneServiceTest {

    private FusinonCloudAccess access;
    private Gson gson;

    @Before
    public void BeforeClass(){
        String osProjectDomainName = "default";
        String osUserDomainName = "default";
        String osUserName = "admin";
        String osPassword = "123456";
        String serverIp = "192.168.15.248";
        String port = "5000";
        boolean is_openstack = true;
        String osProjectId = "0869f5b3b3d3419fb49275e57002eb92";

        OpenRcBean openRcBean = new OpenRcBean(osProjectId, osProjectDomainName, osUserDomainName, osUserName,
                osPassword, serverIp, port, is_openstack);
        FusinonCloudAccess access = new FusinonCloudAccess(openRcBean);
        this.gson = new Gson();
        this.access = access;
    }

    private InstanceModel model2Clone;

    @Test
    public void initCloneServiceTest(){
        String modelStr = "{\"detail\":{\"id\":\"6d680099-425a-4092-8b42-480620027fa4\",\"name\":\"test4fusioncloudv3\",\"tenantId\":\"0869f5b3b3d3419fb49275e57002eb92\",\"host\":\"localhost.localdomain\",\"hostId\":\"edd4ff9dd1fc7ab1d28aa89e6302a2f4ec255286a6cc689712eae93f\",\"availabilityZone\":\"nova\",\"status\":\"ACTIVE\",\"powerState\":\"1\",\"osExtendedVolumesAttached\":[\"7f2d40ee-a943-417d-9ed7-70e03922ad4d\",\"46e5c652-ad90-481a-b4cc-17d896486b50\",\"6e72ad64-2218-4976-b56f-79a2e99a3883\"],\"securityGroups\":[{\"name\":\"default\"}],\"flavorId\":\"75129f8a-914c-449e-a4bc-ba222da3a0ea\"},\"volDiffBitmap\":{\"6e72ad64-2218-4976-b56f-79a2e99a3883\":[],\"7f2d40ee-a943-417d-9ed7-70e03922ad4d\":[{\"key\":4504682496,\"value\":4194304},{\"key\":4638900224,\"value\":4194304},{\"key\":4437573632,\"value\":4194304},{\"key\":4571791360,\"value\":4194304},{\"key\":4706009088,\"value\":4194304},{\"key\":4773117952,\"value\":4194304},{\"key\":62914560,\"value\":4194304},{\"key\":2613051392,\"value\":4194304},{\"key\":3552575488,\"value\":4194304},{\"key\":1807745024,\"value\":4194304},{\"key\":2680160256,\"value\":4194304},{\"key\":1740636160,\"value\":4194304},{\"key\":1673527296,\"value\":4194304},{\"key\":2143289344,\"value\":4194304},{\"key\":1941962752,\"value\":4194304},{\"key\":2814377984,\"value\":4194304},{\"key\":3686793216,\"value\":4194304},{\"key\":2009071616,\"value\":4194304},{\"key\":2076180480,\"value\":4194304},{\"key\":3753902080,\"value\":4194304},{\"key\":2747269120,\"value\":4194304},{\"key\":1874853888,\"value\":4194304},{\"key\":3619684352,\"value\":4194304},{\"key\":4760535040,\"value\":4194304},{\"key\":4693426176,\"value\":4194304},{\"key\":4626317312,\"value\":4194304},{\"key\":4424990720,\"value\":4194304},{\"key\":4492099584,\"value\":4194304},{\"key\":5364514816,\"value\":4194304},{\"key\":4559208448,\"value\":4194304},{\"key\":2759852032,\"value\":4194304},{\"key\":1820327936,\"value\":4194304},{\"key\":3632267264,\"value\":4194304},{\"key\":1954545664,\"value\":4194304},{\"key\":2826960896,\"value\":4194304},{\"key\":3699376128,\"value\":4194304},{\"key\":1887436800,\"value\":4194304},{\"key\":3565158400,\"value\":4194304},{\"key\":8388608,\"value\":4194304},{\"key\":2558525440,\"value\":4194304},{\"key\":1686110208,\"value\":4194304},{\"key\":1619001344,\"value\":4194304},{\"key\":545259520,\"value\":4194304},{\"key\":2692743168,\"value\":4194304},{\"key\":75497472,\"value\":4194304},{\"key\":2625634304,\"value\":4194304},{\"key\":3498049536,\"value\":4194304},{\"key\":1753219072,\"value\":4194304},{\"key\":2088763392,\"value\":4194304},{\"key\":2021654528,\"value\":4194304},{\"key\":3766484992,\"value\":4194304},{\"key\":2839543808,\"value\":2953216},{\"key\":4747952128,\"value\":4194304},{\"key\":4815060992,\"value\":4194304},{\"key\":4680843264,\"value\":4194304},{\"key\":4479516672,\"value\":4194304},{\"key\":4546625536,\"value\":4194304},{\"key\":4613734400,\"value\":4194304},{\"key\":1916796928,\"value\":4194304},{\"key\":3795845120,\"value\":4194304},{\"key\":2118123520,\"value\":4194304},{\"key\":2051014656,\"value\":4194304},{\"key\":3527409664,\"value\":4194304},{\"key\":1782579200,\"value\":4194304},{\"key\":2654994432,\"value\":4194304},{\"key\":3594518528,\"value\":4194304},{\"key\":1849688064,\"value\":4194304},{\"key\":2722103296,\"value\":4194304},{\"key\":104857600,\"value\":4194304},{\"key\":1983905792,\"value\":4194304},{\"key\":3661627392,\"value\":4194304},{\"key\":2789212160,\"value\":4194304},{\"key\":3728736256,\"value\":4194304},{\"key\":1648361472,\"value\":4194304},{\"key\":37748736,\"value\":4194304},{\"key\":1715470336,\"value\":4194304},{\"key\":2587885568,\"value\":4194304},{\"key\":4735369216,\"value\":4194304},{\"key\":4601151488,\"value\":4194304},{\"key\":4534042624,\"value\":4194304},{\"key\":4802478080,\"value\":4194304},{\"key\":4668260352,\"value\":4194304},{\"key\":4466933760,\"value\":4194304},{\"key\":3808428032,\"value\":4194304},{\"key\":3741319168,\"value\":4194304},{\"key\":1660944384,\"value\":4194304},{\"key\":2801795072,\"value\":4194304},{\"key\":3674210304,\"value\":4194304},{\"key\":1996488704,\"value\":4194304},{\"key\":2063597568,\"value\":4194304},{\"key\":2130706432,\"value\":4194304},{\"key\":2600468480,\"value\":4194304},{\"key\":50331648,\"value\":4194304},{\"key\":1795162112,\"value\":4194304},{\"key\":1862270976,\"value\":4194304},{\"key\":1929379840,\"value\":4194304},{\"key\":2734686208,\"value\":4194304},{\"key\":3607101440,\"value\":4194304},{\"key\":3539992576,\"value\":4194304},{\"key\":2667577344,\"value\":4194304},{\"key\":1728053248,\"value\":4194304},{\"key\":4521459712,\"value\":4194304},{\"key\":4722786304,\"value\":4194304},{\"key\":4655677440,\"value\":4194304},{\"key\":4789895168,\"value\":4194304},{\"key\":4454350848,\"value\":4194304},{\"key\":4588568576,\"value\":4194304},{\"key\":1958739968,\"value\":4194304},{\"key\":2696937472,\"value\":4194304},{\"key\":2831155200,\"value\":4194304},{\"key\":3703570432,\"value\":4194304},{\"key\":3770679296,\"value\":4194304},{\"key\":2025848832,\"value\":4194304},{\"key\":1824522240,\"value\":4194304},{\"key\":79691776,\"value\":4194304},{\"key\":2629828608,\"value\":4194304},{\"key\":3502243840,\"value\":4194304},{\"key\":3569352704,\"value\":4194304},{\"key\":1891631104,\"value\":4194304},{\"key\":2764046336,\"value\":4194304},{\"key\":3636461568,\"value\":4194304},{\"key\":1690304512,\"value\":4194304},{\"key\":1623195648,\"value\":4194304},{\"key\":12582912,\"value\":4194304},{\"key\":2562719744,\"value\":4194304},{\"key\":1757413376,\"value\":4194304},{\"key\":2092957696,\"value\":4194304},{\"key\":4508876800,\"value\":4194304},{\"key\":4777312256,\"value\":4194304},{\"key\":4643094528,\"value\":4194304},{\"key\":4575985664,\"value\":4194304},{\"key\":4441767936,\"value\":4194304},{\"key\":4710203392,\"value\":4194304},{\"key\":2776629248,\"value\":4194304},{\"key\":3783262208,\"value\":4194304},{\"key\":2105540608,\"value\":4194304},{\"key\":3649044480,\"value\":4194304},{\"key\":1904214016,\"value\":4194304},{\"key\":3581935616,\"value\":4194304},{\"key\":1837105152,\"value\":4194304},{\"key\":2709520384,\"value\":4194304},{\"key\":1635778560,\"value\":4194304},{\"key\":25165824,\"value\":4194304},{\"key\":2575302656,\"value\":4194304},{\"key\":1702887424,\"value\":4194304},{\"key\":92274688,\"value\":4194304},{\"key\":2642411520,\"value\":4194304},{\"key\":3514826752,\"value\":4194304},{\"key\":1769996288,\"value\":4194304},{\"key\":2038431744,\"value\":4194304},{\"key\":3716153344,\"value\":4194304},{\"key\":1971322880,\"value\":4194304},{\"key\":4429185024,\"value\":4194304},{\"key\":4697620480,\"value\":4194304},{\"key\":4630511616,\"value\":4194304},{\"key\":4764729344,\"value\":4194304},{\"key\":4496293888,\"value\":4194304},{\"key\":4563402752,\"value\":4194304},{\"key\":1732247552,\"value\":4194304},{\"key\":1665138688,\"value\":4194304},{\"key\":54525952,\"value\":4194304},{\"key\":1799356416,\"value\":4194304},{\"key\":2671771648,\"value\":4194304},{\"key\":3544186880,\"value\":4194304},{\"key\":2604662784,\"value\":4194304},{\"key\":2000683008,\"value\":4194304},{\"key\":2067791872,\"value\":4194304},{\"key\":3745513472,\"value\":4194304},{\"key\":2134900736,\"value\":4194304},{\"key\":2805989376,\"value\":4194304},{\"key\":3678404608,\"value\":4194304},{\"key\":1933574144,\"value\":4194304},{\"key\":1866465280,\"value\":4194304},{\"key\":2738880512,\"value\":4194304},{\"key\":3611295744,\"value\":4194304},{\"key\":268435456,\"value\":3702784},{\"key\":805306368,\"value\":3698688},{\"key\":1073741824,\"value\":2101248},{\"key\":2147483648,\"value\":2359296},{\"key\":4752146432,\"value\":4194304},{\"key\":4685037568,\"value\":4194304},{\"key\":4819255296,\"value\":4194304},{\"key\":4483710976,\"value\":4194304},{\"key\":4550819840,\"value\":4194304},{\"key\":4617928704,\"value\":4194304},{\"key\":67108864,\"value\":4194304},{\"key\":1677721600,\"value\":4194304},{\"key\":1610612736,\"value\":4194304},{\"key\":536870912,\"value\":4194304},{\"key\":2550136832,\"value\":4194304},{\"key\":1811939328,\"value\":4194304},{\"key\":2684354560,\"value\":4194304},{\"key\":0,\"value\":4194304},{\"key\":3489660928,\"value\":4194304},{\"key\":2617245696,\"value\":4194304},{\"key\":1744830464,\"value\":4194304},{\"key\":1879048192,\"value\":4194304},{\"key\":3758096384,\"value\":4194304},{\"key\":2080374784,\"value\":4194304},{\"key\":2013265920,\"value\":4194304},{\"key\":1946157056,\"value\":4194304},{\"key\":2751463424,\"value\":4194304},{\"key\":3623878656,\"value\":4194304},{\"key\":2818572288,\"value\":4194304},{\"key\":3690987520,\"value\":4194304},{\"key\":3556769792,\"value\":4194304},{\"key\":549453824,\"value\":2269184},{\"key\":4739563520,\"value\":4194304},{\"key\":4806672384,\"value\":4194304},{\"key\":4471128064,\"value\":4194304},{\"key\":4538236928,\"value\":4194304},{\"key\":4605345792,\"value\":4194304},{\"key\":4672454656,\"value\":4194304},{\"key\":96468992,\"value\":4194304},{\"key\":2109734912,\"value\":4194304},{\"key\":2042626048,\"value\":4194304},{\"key\":3720347648,\"value\":4194304},{\"key\":3787456512,\"value\":4194304},{\"key\":1707081728,\"value\":4194304},{\"key\":29360128,\"value\":4194304},{\"key\":1774190592,\"value\":4194304},{\"key\":2579496960,\"value\":4194304},{\"key\":1841299456,\"value\":4194304},{\"key\":2713714688,\"value\":4194304},{\"key\":3586129920,\"value\":4194304},{\"key\":3519021056,\"value\":4194304},{\"key\":2646605824,\"value\":4194304},{\"key\":1908408320,\"value\":4194304},{\"key\":2780823552,\"value\":4194304},{\"key\":3653238784,\"value\":4194304},{\"key\":1975517184,\"value\":4194304},{\"key\":1639972864,\"value\":4194304},{\"key\":3812622336,\"value\":1789952},{\"key\":4525654016,\"value\":4194304},{\"key\":4726980608,\"value\":4194304},{\"key\":4794089472,\"value\":4194304},{\"key\":4592762880,\"value\":4194304},{\"key\":4659871744,\"value\":4194304},{\"key\":4458545152,\"value\":4194304},{\"key\":1920991232,\"value\":4194304},{\"key\":2122317824,\"value\":4194304},{\"key\":3800039424,\"value\":4194304},{\"key\":2055208960,\"value\":4194304},{\"key\":109051904,\"value\":4194304},{\"key\":3598712832,\"value\":4194304},{\"key\":1853882368,\"value\":4194304},{\"key\":2726297600,\"value\":4194304},{\"key\":2793406464,\"value\":4194304},{\"key\":3665821696,\"value\":4194304},{\"key\":1988100096,\"value\":4194304},{\"key\":3732930560,\"value\":4194304},{\"key\":1719664640,\"value\":4194304},{\"key\":2592079872,\"value\":4194304},{\"key\":41943040,\"value\":4194304},{\"key\":1786773504,\"value\":4194304},{\"key\":2659188736,\"value\":4194304},{\"key\":3531603968,\"value\":4194304},{\"key\":1652555776,\"value\":4194304},{\"key\":4513071104,\"value\":4194304},{\"key\":4714397696,\"value\":4194304},{\"key\":4781506560,\"value\":4194304},{\"key\":4445962240,\"value\":4194304},{\"key\":4580179968,\"value\":4194304},{\"key\":4647288832,\"value\":4194304},{\"key\":1950351360,\"value\":4194304},{\"key\":1681915904,\"value\":4194304},{\"key\":2755657728,\"value\":4194304},{\"key\":3628072960,\"value\":4194304},{\"key\":2822766592,\"value\":4194304},{\"key\":3695181824,\"value\":4194304},{\"key\":2017460224,\"value\":4194304},{\"key\":3762290688,\"value\":4194304},{\"key\":71303168,\"value\":4194304},{\"key\":2621440000,\"value\":4194304},{\"key\":3493855232,\"value\":4194304},{\"key\":4194304,\"value\":4194304},{\"key\":1749024768,\"value\":4194304},{\"key\":1883242496,\"value\":4194304},{\"key\":2688548864,\"value\":4194304},{\"key\":3560964096,\"value\":4194304},{\"key\":1816133632,\"value\":4194304},{\"key\":2554331136,\"value\":4194304},{\"key\":541065216,\"value\":4194304},{\"key\":1614807040,\"value\":4194304},{\"key\":2084569088,\"value\":4194304},{\"key\":4433379328,\"value\":4194304},{\"key\":4500488192,\"value\":4194304},{\"key\":4567597056,\"value\":4194304},{\"key\":4634705920,\"value\":4194304},{\"key\":4701814784,\"value\":4194304},{\"key\":4768923648,\"value\":4194304},{\"key\":1761607680,\"value\":4194304},{\"key\":2030043136,\"value\":4194304},{\"key\":1627389952,\"value\":4194304},{\"key\":1694498816,\"value\":4194304},{\"key\":16777216,\"value\":4194304},{\"key\":2566914048,\"value\":4194304},{\"key\":83886080,\"value\":4194304},{\"key\":1828716544,\"value\":4194304},{\"key\":2701131776,\"value\":4194304},{\"key\":3573547008,\"value\":4194304},{\"key\":3506438144,\"value\":4194304},{\"key\":2634022912,\"value\":4194304},{\"key\":1962934272,\"value\":4194304},{\"key\":2768240640,\"value\":4194304},{\"key\":3640655872,\"value\":4194304},{\"key\":2835349504,\"value\":4194304},{\"key\":3707764736,\"value\":4194304},{\"key\":3774873600,\"value\":4194304},{\"key\":1895825408,\"value\":4194304},{\"key\":2097152000,\"value\":4194304},{\"key\":4756340736,\"value\":4194304},{\"key\":4487905280,\"value\":4194304},{\"key\":4555014144,\"value\":4194304},{\"key\":4622123008,\"value\":4194304},{\"key\":4689231872,\"value\":4194304},{\"key\":1992294400,\"value\":4194304},{\"key\":1656750080,\"value\":4194304},{\"key\":2059403264,\"value\":4194304},{\"key\":3804233728,\"value\":4194304},{\"key\":3670016000,\"value\":4194304},{\"key\":1925185536,\"value\":4194304},{\"key\":2797600768,\"value\":4194304},{\"key\":3602907136,\"value\":4194304},{\"key\":3737124864,\"value\":4194304},{\"key\":2126512128,\"value\":4194304},{\"key\":1723858944,\"value\":4194304},{\"key\":2596274176,\"value\":4194304},{\"key\":46137344,\"value\":4194304},{\"key\":1790967808,\"value\":4194304},{\"key\":2663383040,\"value\":4194304},{\"key\":1858076672,\"value\":4194304},{\"key\":2730491904,\"value\":4194304},{\"key\":3535798272,\"value\":4194304},{\"key\":809500672,\"value\":1056768},{\"key\":4743757824,\"value\":4194304},{\"key\":4810866688,\"value\":4194304},{\"key\":4475322368,\"value\":4194304},{\"key\":4542431232,\"value\":4194304},{\"key\":4609540096,\"value\":4194304},{\"key\":4676648960,\"value\":4194304},{\"key\":2675965952,\"value\":4194304},{\"key\":1669332992,\"value\":4194304},{\"key\":58720256,\"value\":4194304},{\"key\":1803550720,\"value\":4194304},{\"key\":3548381184,\"value\":4194304},{\"key\":1870659584,\"value\":4194304},{\"key\":1937768448,\"value\":4194304},{\"key\":2004877312,\"value\":4194304},{\"key\":2810183680,\"value\":4194304},{\"key\":3682598912,\"value\":4194304},{\"key\":3749707776,\"value\":4194304},{\"key\":3615490048,\"value\":4194304},{\"key\":2743074816,\"value\":4194304},{\"key\":2608857088,\"value\":4194304},{\"key\":1736441856,\"value\":4194304},{\"key\":2139095040,\"value\":4194304},{\"key\":2071986176,\"value\":4194304},{\"key\":4823449600,\"value\":2699264},{\"key\":4462739456,\"value\":4194304},{\"key\":4529848320,\"value\":4194304},{\"key\":4596957184,\"value\":4194304},{\"key\":4664066048,\"value\":4194304},{\"key\":4731174912,\"value\":4194304},{\"key\":4798283776,\"value\":4194304},{\"key\":3644850176,\"value\":4194304},{\"key\":2034237440,\"value\":4194304},{\"key\":3711959040,\"value\":4194304},{\"key\":1967128576,\"value\":4194304},{\"key\":2772434944,\"value\":4194304},{\"key\":3779067904,\"value\":4194304},{\"key\":2101346304,\"value\":4194304},{\"key\":1631584256,\"value\":4194304},{\"key\":1698693120,\"value\":4194304},{\"key\":20971520,\"value\":4194304},{\"key\":2571108352,\"value\":4194304},{\"key\":88080384,\"value\":4194304},{\"key\":2638217216,\"value\":4194304},{\"key\":3510632448,\"value\":4194304},{\"key\":1765801984,\"value\":4194304},{\"key\":1832910848,\"value\":4194304},{\"key\":2705326080,\"value\":4194304},{\"key\":3577741312,\"value\":4194304},{\"key\":1900019712,\"value\":4194304},{\"key\":4517265408,\"value\":4194304},{\"key\":4450156544,\"value\":4194304},{\"key\":4584374272,\"value\":4194304},{\"key\":4651483136,\"value\":4194304},{\"key\":4718592000,\"value\":4194304},{\"key\":4785700864,\"value\":4194304},{\"key\":1979711488,\"value\":4194304},{\"key\":2113929216,\"value\":4194304},{\"key\":1644167168,\"value\":4194304},{\"key\":1711276032,\"value\":4194304},{\"key\":3724541952,\"value\":4194304},{\"key\":2785017856,\"value\":4194304},{\"key\":3657433088,\"value\":4194304},{\"key\":2046820352,\"value\":4194304},{\"key\":3791650816,\"value\":4194304},{\"key\":1912602624,\"value\":4194304},{\"key\":3590324224,\"value\":4194304},{\"key\":1845493760,\"value\":4194304},{\"key\":2717908992,\"value\":4194304},{\"key\":100663296,\"value\":4194304},{\"key\":3523215360,\"value\":4194304},{\"key\":1778384896,\"value\":4194304},{\"key\":33554432,\"value\":4194304},{\"key\":2583691264,\"value\":4194304},{\"key\":2650800128,\"value\":4194304},{\"key\":113246208,\"value\":3375104}],\"46e5c652-ad90-481a-b4cc-17d896486b50\":[{\"key\":146800640,\"value\":4194304},{\"key\":297795584,\"value\":4194304},{\"key\":364904448,\"value\":4194304},{\"key\":12582912,\"value\":4194304},{\"key\":163577856,\"value\":4194304},{\"key\":314572800,\"value\":4194304},{\"key\":96468992,\"value\":4194304},{\"key\":465567744,\"value\":4194304},{\"key\":29360128,\"value\":4194304},{\"key\":180355072,\"value\":4194304},{\"key\":398458880,\"value\":4194304},{\"key\":247463936,\"value\":4194304},{\"key\":381681664,\"value\":4194304},{\"key\":230686720,\"value\":4194304},{\"key\":515899392,\"value\":4194304},{\"key\":79691776,\"value\":4194304},{\"key\":448790528,\"value\":4194304},{\"key\":213909504,\"value\":4194304},{\"key\":432013312,\"value\":4194304},{\"key\":62914560,\"value\":4194304},{\"key\":281018368,\"value\":4194304},{\"key\":499122176,\"value\":4194304},{\"key\":348127232,\"value\":4194304},{\"key\":197132288,\"value\":4194304},{\"key\":46137344,\"value\":4194304},{\"key\":264241152,\"value\":4194304},{\"key\":482344960,\"value\":4194304},{\"key\":331350016,\"value\":4194304},{\"key\":113246208,\"value\":4194304},{\"key\":1069547520,\"value\":4194304},{\"key\":415236096,\"value\":4194304},{\"key\":130023424,\"value\":4194304},{\"key\":545259520,\"value\":2129920},{\"key\":142606336,\"value\":4194304},{\"key\":360710144,\"value\":4194304},{\"key\":377487360,\"value\":4194304},{\"key\":159383552,\"value\":4194304},{\"key\":528482304,\"value\":4194304},{\"key\":92274688,\"value\":4194304},{\"key\":243269632,\"value\":4194304},{\"key\":461373440,\"value\":4194304},{\"key\":25165824,\"value\":4194304},{\"key\":394264576,\"value\":4194304},{\"key\":176160768,\"value\":4194304},{\"key\":109051904,\"value\":4194304},{\"key\":327155712,\"value\":4194304},{\"key\":310378496,\"value\":4194304},{\"key\":75497472,\"value\":4194304},{\"key\":293601280,\"value\":4194304},{\"key\":511705088,\"value\":4194304},{\"key\":8388608,\"value\":4194304},{\"key\":226492416,\"value\":4194304},{\"key\":444596224,\"value\":4194304},{\"key\":209715200,\"value\":4194304},{\"key\":427819008,\"value\":4194304},{\"key\":125829120,\"value\":4194304},{\"key\":343932928,\"value\":4194304},{\"key\":192937984,\"value\":4194304},{\"key\":411041792,\"value\":4194304},{\"key\":41943040,\"value\":4194304},{\"key\":260046848,\"value\":4194304},{\"key\":478150656,\"value\":4194304},{\"key\":494927872,\"value\":4194304},{\"key\":58720256,\"value\":4194304},{\"key\":276824064,\"value\":4194304},{\"key\":306184192,\"value\":4194304},{\"key\":71303168,\"value\":4194304},{\"key\":289406976,\"value\":4194304},{\"key\":507510784,\"value\":4194304},{\"key\":20971520,\"value\":4194304},{\"key\":239075328,\"value\":4194304},{\"key\":457179136,\"value\":4194304},{\"key\":171966464,\"value\":4194304},{\"key\":390070272,\"value\":4194304},{\"key\":322961408,\"value\":4194304},{\"key\":104857600,\"value\":4194304},{\"key\":541065216,\"value\":4194304},{\"key\":671088640,\"value\":16384},{\"key\":88080384,\"value\":4194304},{\"key\":524288000,\"value\":4194304},{\"key\":805306368,\"value\":16384},{\"key\":440401920,\"value\":4194304},{\"key\":222298112,\"value\":4194304},{\"key\":4194304,\"value\":4194304},{\"key\":155189248,\"value\":4194304},{\"key\":373293056,\"value\":4194304},{\"key\":138412032,\"value\":4194304},{\"key\":356515840,\"value\":4194304},{\"key\":939524096,\"value\":16384},{\"key\":54525952,\"value\":4194304},{\"key\":272629760,\"value\":4194304},{\"key\":490733568,\"value\":4194304},{\"key\":121634816,\"value\":4194304},{\"key\":268435456,\"value\":16384},{\"key\":339738624,\"value\":4194304},{\"key\":188743680,\"value\":4194304},{\"key\":37748736,\"value\":4194304},{\"key\":255852544,\"value\":4194304},{\"key\":473956352,\"value\":4194304},{\"key\":402653184,\"value\":16384},{\"key\":406847488,\"value\":4194304},{\"key\":205520896,\"value\":4194304},{\"key\":423624704,\"value\":4194304},{\"key\":134217728,\"value\":4190208},{\"key\":532676608,\"value\":106496},{\"key\":436207616,\"value\":4194304},{\"key\":67108864,\"value\":4194304},{\"key\":0,\"value\":4194304},{\"key\":369098752,\"value\":4194304},{\"key\":150994944,\"value\":4194304},{\"key\":520093696,\"value\":4194304},{\"key\":83886080,\"value\":4194304},{\"key\":234881024,\"value\":4194304},{\"key\":452984832,\"value\":4194304},{\"key\":16777216,\"value\":4194304},{\"key\":385875968,\"value\":4194304},{\"key\":318767104,\"value\":4194304},{\"key\":536870912,\"value\":4194304},{\"key\":167772160,\"value\":4194304},{\"key\":100663296,\"value\":4194304},{\"key\":469762048,\"value\":4194304},{\"key\":33554432,\"value\":4194304},{\"key\":251658240,\"value\":4194304},{\"key\":301989888,\"value\":4194304},{\"key\":218103808,\"value\":4194304},{\"key\":201326592,\"value\":4194304},{\"key\":419430400,\"value\":4194304},{\"key\":50331648,\"value\":4194304},{\"key\":486539264,\"value\":4194304},{\"key\":117440512,\"value\":4194304},{\"key\":335544320,\"value\":4194304},{\"key\":184549376,\"value\":4194304},{\"key\":352321536,\"value\":4194304},{\"key\":285212672,\"value\":4194304},{\"key\":503316480,\"value\":4194304}]},\"ports\":[{\"id\":\"0295b2e4-b33f-48bc-a61d-cd29d71354d7\",\"name\":\"\",\"networkId\":\"f16bf9df-8fff-4c15-9cf2-387b47799823\",\"fixedIps\":[{\"ipAddress\":\"192.168.12.28\",\"subnetId\":\"4922a1d2-b235-4d15-82b1-d5f17569c49d\"}],\"state\":\"ACTIVE\"}],\"volumeDetailList\":[{\"id\":\"7f2d40ee-a943-417d-9ed7-70e03922ad4d\",\"size\":5,\"name\":\"test4fusioncloudv2\",\"status\":\"IN_USE\",\"attachments\":[{\"device\":\"/dev/vda\",\"id\":\"7f2d40ee-a943-417d-9ed7-70e03922ad4d\",\"server_id\":\"6d680099-425a-4092-8b42-480620027fa4\",\"attachment_id\":\"19c0840d-f989-44f2-9856-a53bf277ad58\",\"volume_id\":\"7f2d40ee-a943-417d-9ed7-70e03922ad4d\"}],\"bootable\":true,\"poolName\":\"volumes\"},{\"id\":\"46e5c652-ad90-481a-b4cc-17d896486b50\",\"volumeType\":\"ceph\",\"size\":1,\"name\":\"test4fusioncloudv4\",\"status\":\"IN_USE\",\"attachments\":[{\"device\":\"/dev/vdc\",\"id\":\"46e5c652-ad90-481a-b4cc-17d896486b50\",\"server_id\":\"6d680099-425a-4092-8b42-480620027fa4\",\"attachment_id\":\"ece0e85e-6863-4ba8-99f6-8adbdb9f3009\",\"volume_id\":\"46e5c652-ad90-481a-b4cc-17d896486b50\"}],\"bootable\":false,\"poolName\":\"volumes\"},{\"id\":\"6e72ad64-2218-4976-b56f-79a2e99a3883\",\"volumeType\":\"ceph\",\"size\":1,\"name\":\"test4clone-01\",\"status\":\"IN_USE\",\"attachments\":[{\"device\":\"/dev/vdd\",\"id\":\"6e72ad64-2218-4976-b56f-79a2e99a3883\",\"server_id\":\"6d680099-425a-4092-8b42-480620027fa4\",\"attachment_id\":\"a2c7be62-c617-4e44-b926-e17c465a84d7\",\"volume_id\":\"6e72ad64-2218-4976-b56f-79a2e99a3883\"}],\"bootable\":false,\"poolName\":\"volumes\"}]}";
        this.model2Clone = gson.fromJson(modelStr, InstanceModel.class);
    }

    private String cloneSysVolumeId;
    private Map<String, String> volOld2New = new HashMap<>();

    @Test
    public void createVolumesTest() throws
            CreateVolumeException, QueryVolumeException, CreateVolumeTimeoutException {
        initCloneServiceTest();

        for (DetailVolumeResponse detailVolumeResponse : this.model2Clone.getVolumeDetailList()) {
            boolean bootable = Optional.ofNullable(detailVolumeResponse.getBootable()).orElse(false);
            String newVolId = this.access.createEmptyVolume(
                    detailVolumeResponse.getSize(),
                    detailVolumeResponse.getVolumeType(),
                    detailVolumeResponse.getName(),
                    bootable
            );
            if(bootable){
                cloneSysVolumeId = newVolId;
            }
            volOld2New.put(detailVolumeResponse.getId(), newVolId);
        }
    }

    List<String> networkIds = new ArrayList<>();

    @Test
    public void cloneNetworkTest() throws QueryNetworkException {
        boolean accessNetwork = false;
        initCloneServiceTest();

        Map<String, String> availableNetworkMap = this.access.getNetworkList().stream()
                .filter(network -> network.getStatus().equals(State.ACTIVE)).collect(Collectors
                        .toMap(BasicResource::getName, IdEntity::getId,
                                (oldVal, currVal) -> currVal));
        log.info("query available network {}", availableNetworkMap);

        if (accessNetwork) {
            List<String> networkIds = availableNetworkMap.entrySet().stream()
                    .filter(v -> this.model2Clone.getDetail().getAddresses().getAddresses().containsKey(v.getKey()))
                    .map(Map.Entry::getValue).collect(Collectors.toList());

            if (networkIds.isEmpty()) {
                //过滤出ACIVE的网络，然后选择第一个作为克隆机的创建网络
                String defaultNetId = availableNetworkMap.values().stream().findFirst().orElse("");
                networkIds = Lists.newArrayList(defaultNetId);
            }
        } else {
            //过滤出ACIVE的网络，然后选择第一个作为克隆机的创建网络
            String defaultNetId = availableNetworkMap.values().stream().findFirst().orElse("");
            networkIds = Lists.newArrayList(defaultNetId);
        }
    }

    private String cloneInstanceId ;

    @Test
    public void createInstanceTest() throws CreateInstanceException, CreateInstanceTimeoutException, QueryInstanceException,
            QueryVolumeException, CreateVolumeTimeoutException, CreateVolumeException, QueryNetworkException {

        initCloneServiceTest();
        createVolumesTest();
        cloneNetworkTest();

        String instanceName = "test4Clone-001";
        String flavorId = this.model2Clone.getDetail().getFlavorId();
        String cloneInstanceName = instanceName.length() > 32 ? instanceName.substring(0, 29) + "..." : instanceName;
        String defaultPassword = "1qaz@WSX";
        String availabilityZone = this.model2Clone.getDetail().getAvailabilityZone();
        //安全组信息
        List<String> securityGroups = this.model2Clone.getDetail().getSecurityGroups().stream()
                .map(SecurityGroup::getName)
                .collect(Collectors.toList());

        cloneInstanceId = this.access.createInstance(
                cloneInstanceName,
                flavorId,
                defaultPassword,
                networkIds,
                availabilityZone,
                cloneSysVolumeId,
                securityGroups);

        log.info("clone instance id is {}",cloneInstanceId);
    }

    @Test
    public void mountVolumesTest() throws AttachVolumeException,
            QueryVolumeException, AttachVolumeTimeoutException {
        initCloneServiceTest();

        for (DetailVolumeResponse oldVol : this.model2Clone.getVolumeDetailList()) {
            boolean bootable = Optional.ofNullable(oldVol.getBootable()).orElse(false);
            if (!bootable) {
                String volId = this.volOld2New.get(oldVol.getId());
                String device = oldVol.getAttachments().get(0).getDevice();
                log.info("serverId:{} prepare to mount volume :{},device ={} ", this.cloneInstanceId, volId, device);
                this.access.attachVolume(this.cloneInstanceId, volId, device);
            }
        }
    }

    @Test
    public void powerOffTest() throws PowerOffTimeoutException, PowerOffInstanceException, QueryInstanceException {
        String cloneInstanceId = "";

        this.access.powerOffInstance(cloneInstanceId);
    }

    private Map<String, String> src2ImageSpec = new HashMap<>();

    @Test
    public void initImageSpecsTest() throws CreateVolumeException, QueryVolumeException, CreateVolumeTimeoutException {
        createVolumesTest();

        for (Map.Entry<String, String> entry : this.volOld2New.entrySet()) {
            String srcId = entry.getKey();
            String newVolId = entry.getValue();
            String poolName = "volumes";
            String imageSpec = this.getImageSpec(poolName, newVolId);
            this.src2ImageSpec.put(srcId, imageSpec);
        }
    }

    String getImageSpec(String poolName, String resId){
        return String.format("%s/volume-%s", poolName, resId);
    }

    @Test
    public void buildCloneContextTest(){
        //
    }


}
