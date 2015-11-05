package com.panoptech.mmc.anpr;

import com.panoptech.mmc.server.exception.NotAuthorisedServerException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.commons.io.FileUtils;

public class AnprFeeder
{

    static final HexBinaryAdapter adapter = new HexBinaryAdapter();
    private String anprDir;
    private static final String CAR_IMAGE_BASE64 = "0xffd8ffe000104a46494600010100000100010000fffe000d583235384a56562039350affdb0043" +
													"0050373c463c32504641465a55505f78c882786e6e78f5afb991c8ffffffffffffffffffffffffff" +
													"ffffffffffffffffffffffffffffffffffffffffffffffffffffdb004301555a5a786978eb8282eb" +
													"ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
													"ffffffffffffffffffffffffffffffc00011080120016803012200021101031101ffc4001f000001" +
													"0501010101010100000000000000000102030405060708090a0bffc400b510000201030302040305" +
													"0504040000017d01020300041105122131410613516107227114328191a1082342b1c11552d1f024" +
													"33627282090a161718191a25262728292a3435363738393a434445464748494a535455565758595a" +
													"636465666768696a737475767778797a838485868788898a92939495969798999aa2a3a4a5a6a7a8" +
													"a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae1e2e3e4e5e6e7e8e9eaf1" +
													"f2f3f4f5f6f7f8f9faffc4001f010003010101010101010101000000000000010203040506070809" +
													"0a0bffc400b511000201020404030407050404000102770001020311040521310612415107617113" +
													"22328108144291a1b1c109233352f0156272d10a162434e125f11718191a262728292a3536373839" +
													"3a434445464748494a535455565758595a636465666768696a737475767778797a82838485868788" +
													"898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3" +
													"d4d5d6d7d8d9dae2e3e4e5e6e7e8e9eaf2f3f4f5f6f7f8f9faffda000c03010002110311003f00ae" +
													"3a5140e828a0028a28a0028a28a0028a28a00296928a005a28a2800a28a2800a28a280128a5a4a00" +
													"28a28a0028a28a0028a28a0028a28a0028a28a0028a28a0028a28a0028a28a0028a28a0028a28a00" +
													"2928a2800a28a2800a28a2800a28a280147414503a0a5a004a28a2800a28a2800a28a5a0028a28a0" +
													"02969296800a4a5a4a0028a5a280128a28a004a2968a004a28a2800a28a5a004a29692800a28a280" +
													"0a28a2800a28a2800a28a2800a28a2800a28a280128a5a4a0028a28a0028a28a0028a28a0070e828" +
													"a0741450014514b400945145001451450014514b40051451400514b49400514514009452d1400945" +
													"145001451450014514500145145002514b49400514b4500145145002514b45002514514005145140" +
													"0514514009452d25001451450014514500387414b483a0a5a0028a29680128a5a4a0028a28a0028a" +
													"2968012968a5a004a2968a004a2969280128a5a280128a5a4a0028a5a2801292968a004a28a2800a" +
													"28a2800a2968a004a28a2800a4a5a280128a28a0028a28a0028a28a0028a28a004a28a2800a28a28" +
													"01c3a0a5a41d052d002d1451400514b45002514b45002514b45001452d0050018a314ec518a006e2" +
													"929d8a31400dc514b8a31400da29d8a4c5002514b450025252d14009452d14009452d14009451450" +
													"02514b49400514514009452d14009451450014514500145145002514b45002514b450028e8296907" +
													"414b40052d140a00514b494ea0031498a7518a006e29714ec518a006e2940a7014a05002628c53c0" +
													"a5c500478a31526da4c500478a4c5498a4c500478a314fc5262801b8a4a7629314009498a7628c50" +
													"0371453a92801292969280128a5a280128a5a2801292968a004a2968a004a4a5a280128a5a280128" +
													"a28a0028a28a0028a28a0051d052d20e829680168a296800a70a6d3850028a7520a70a00314b8a50" +
													"29c0500340a50b4f0b4e0b400c0b4edb4f02968023db46da928a0088ad34ad4d8a42b4010914d22a" +
													"52b4d2280222293152114dc500371494ea4a006d14b494009494ea4a004a4a7518a006d2e2971462" +
													"801b8a314fc518a006628a762931400da29d8a4c500368a5a280128a28a004a29692800a28a28014" +
													"741f4a5a41d052d002d2d252d0028a514829c2801453c534538500380a78148b5228a0000a751450" +
													"01451450014514500145145002119a6115252119a00848a69152114c3400c34869c69314086e2929" +
													"d8a422801b4629d462801b8a5c52e2940a063714ec5380a7014011e28db52eda36d0044569a454c5" +
													"690ad00424537152914c228019494e22928012929692800a4a5a4a0028a28a0070e8296917a0a5a0" +
													"029692968014538520a70a0070a70a68a78a007a8a9074a628a92800a28a2800a28a2800a28a2800" +
													"a28a2800a28a28018c2986a53d2a3340119a294d25020c534d3a9a6801b4b4528a0000a7014014e0" +
													"2818014f0b4aa29d4008168db4ea28018569a56a5a422802022a322a7615130a00888a69a7b0a61a" +
													"006d253a92801292968a004a28a2801cbd052d22fdd1f4a5a005a51494a280145385345385003854" +
													"8b518a91680245a7d3169f400514514005145140051451400514514005145140094c6a92a36a0061" +
													"a4a534da0414d34b9a4a0029452528a0070a914530548b40c70a5a28a0028a28a0028a28a006b0a8" +
													"5854e7a544d4010b5466a56a8cd0030d2529a4a004a28a280128a5a280157ee8a5a45fba29680169" +
													"45251400e14e14d14a2801e2a45a88548b4012ad3ea3534fa005a28a2800a28a2800a28a2800a28a" +
													"2800a28a2801298d4f3d2a36a0069a61a71a69a004a28a4a042d28a6d385031e2a55a885489400fa" +
													"28a2800a28a2800a28a28010f4a89aa53d2a26a0089aa335235466801a69b4e34da004a28a2800a2" +
													"8a28014741f4a5a68e8296801696928a007528a6d28a0078a783518a78a00954d4a0f150a9a914d0" +
													"03e8a28a0028a28a0028a28a0028a28a0028a29280118d4669cc6986801a69a694d2500252514500" +
													"2d28a6d283400f06a45351034f068026a5a62b53a80168a28a0028a29a4d00231a898d3d8d44c680" +
													"1ad519a731a69a0069a4a5a4a004a28a280128a28a0051d052d20e8296800a5a4a5a005a51494a28" +
													"01c29c29829c0d00480d3c1a881a703401306a75440d383500494520345002d14514005149485a80" +
													"169a4d216a693400134d268269a4d0021a434a69a6800a4a2928016969b466801e0d381a8c1a7034" +
													"012834f0d50834a1a8027dd46ea8b751ba80242d4c2d4d2d4d2d400a4d309a09a613400134d34134" +
													"8680128a292800a28a4a0028a28a0051d05140e8296800a5a4a5a005a292968016941a6d2e6801e0" +
													"d2834ccd2834012034e06a2069c0d004a1a9435440d2e68025dd4bbaa2cd19a0090b5349a6e69a4d" +
													"00389a4cd349a4cd003b3499a6e68cd0029a69a5cd250025252d2500145251400b4b9a6d2e6801e0" +
													"d2e6a3cd2e68024cd19a8f3466801f9a4269b9a42680149a69346692800a4a28a002928a2800a4a5" +
													"a4a0028a28a0051d052d20e8296800a5a4a280168a4a2801696928a005a5a4a2801d9a5cd368a007" +
													"e6941a6668cd00499a334ccd2e6801d9a426933484d002e69334949400b9a334946680168a4cd140" +
													"0b49451400945149400b45251400b45251400b9a2928a005a4a28a002928a4a005a4a28a0028a28a" +
													"002928a2800a28a280147414b483a0a5a0028a28a0028a28a005a29296800a28a280168a4a280169" +
													"6928a005a5a4a2801d4868a2801292834500145251400b45251400b45252d0014514940051451400" +
													"514945002d1494500145145001494b494005145140051494500145145001451450028e8296907414" +
													"b4005145140051451400b45145001451450014514b4005145140051451400b4514500252538d2500" +
													"2514514005145140051494b40051451400514514005252d140094514500145145001494b49400514" +
													"514005252d2500145145001451450028e828a074145002d1494b40051451400b45252d0014514500" +
													"14b494b40051451400b45145001451450006929690d0025145140051451400514514005145140051" +
													"452d002514b45002514b450025252d14009452d25002514b49400514514005252d1400945145007f" +
													"ffd9a4a004a28a280128a5a280157ee8a5a45fba2968016945251400e14e14d14a2801e2a45a8854" +
													"8b4012ad3ea3534fa005a28a2800a28a2800a28a2800a28a2800a28a2801298d4f3d2a36a0069a61" +
													"a71a69a004a28a4a042d28a6d385031e2a55a885489400fa28a2800a28a2800a28a28010f4a89aa5" +
													"3d2a26a0089aa335235466801a69b4e34da004a28a2800a28a28014741f4a5a68e8296801696928a" +
													"007528a6d28a0078a783518a78a00954d4a0f150a9a914d003e8a28a0028a28a00"
													;

    public AnprFeeder(String anprDir)
    {
        this.anprDir = anprDir;
    }

    public void send(String camera, String plate, float lat, float lon)
        throws NotAuthorisedServerException, IOException
    {
        File testAnprXmlFile = createTestFile(camera, plate, plate, lat, lon);
        if(anprDir != null)
        {
            File dest = new File((new StringBuilder(String.valueOf(anprDir))).append(File.separator).append(System.currentTimeMillis()).append(".xml").toString());
            FileUtils.copyFile(testAnprXmlFile, dest);
        }
    }

    private File createTestFile(String cameraName, String carNumber1, String carNumber2, float lat, float lon)
        throws IOException
    {
        File testAnprXmlFile = File.createTempFile((new StringBuilder("anpr_test_")).append(System.currentTimeMillis()).toString(), "xml");
        FileWriter fw = new FileWriter(testAnprXmlFile);
        fw.write(createXmlString(cameraName, carNumber1, carNumber2, lat, lon));
        fw.close();
        return testAnprXmlFile;
    }

    private String createXmlString(String cameraName, String carNumber1, String carNumber2, float lat, float lon)
        throws IOException
    {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SSS");
        String timeStampStr = dateTimeFormat.format(new Date(System.currentTimeMillis()));
        String longStr = String.format("%.6f", new Object[] {
            Float.valueOf(lon)
        });
        String latStr = String.format("%.6f", new Object[] {
            Float.valueOf(lat)
        });
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        strBuf.append("<PlateExport CDType=\"CDM\" User=\"User1\" Client=\"System1\" Version=\"5.0.1.42" +
".201105122056\" Update=\"0\">\n"
);
        strBuf.append("   <InfoCar>\n");
        strBuf.append("      <Record>\n");
        strBuf.append("         <EventID>{BE5B7103-5952-4d15-826F-BC6CE5169342}</EventID>\n");
        strBuf.append("         <CarNumber>").append(carNumber1).append("</CarNumber>\n");
        strBuf.append("         <CarNumber_2>").append(carNumber2).append("</CarNumber_2>\n");
        strBuf.append("         <X>").append(latStr).append("</X>\n");
        strBuf.append("         <Y>").append(longStr).append("</Y>\n");
        strBuf.append("         <CarColor></CarColor>\n");
        strBuf.append("         <CarSpeed>0</CarSpeed>\n");
        strBuf.append("         <Datetime>").append(timeStampStr).append("</Datetime>\n");
        strBuf.append("         <GMTDatetime>").append(timeStampStr).append("</GMTDatetime>\n");
        strBuf.append("         <Notes></Notes>\n");
        strBuf.append("         <Status></Status>\n");
        strBuf.append("         <OwnerName></OwnerName>\n");
        strBuf.append("         <PlateImage>").append(getImageBase64(carNumber1)).append("</PlateImage>");
        strBuf.append("         <CarImage>").append("0xffd8ffe000104a46494600010100000100010000fffe000d583235384a56562039350affdb0043" +
													"0050373c463c32504641465a55505f78c882786e6e78f5afb991c8ffffffffffffffffffffffffff" +
													"ffffffffffffffffffffffffffffffffffffffffffffffffffffdb004301555a5a786978eb8282eb" +
													"ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
													"ffffffffffffffffffffffffffffffc00011080120016803012200021101031101ffc4001f000001" +
													"0501010101010100000000000000000102030405060708090a0bffc400b510000201030302040305" +
													"0504040000017d01020300041105122131410613516107227114328191a1082342b1c11552d1f024" +
													"33627282090a161718191a25262728292a3435363738393a434445464748494a535455565758595a" +
													"636465666768696a737475767778797a838485868788898a92939495969798999aa2a3a4a5a6a7a8" +
													"a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae1e2e3e4e5e6e7e8e9eaf1" +
													"f2f3f4f5f6f7f8f9faffc4001f010003010101010101010101000000000000010203040506070809" +
													"0a0bffc400b511000201020404030407050404000102770001020311040521310612415107617113" +
													"22328108144291a1b1c109233352f0156272d10a162434e125f11718191a262728292a3536373839" +
													"3a434445464748494a535455565758595a636465666768696a737475767778797a82838485868788" +
													"898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3" +
													"d4d5d6d7d8d9dae2e3e4e5e6e7e8e9eaf2f3f4f5f6f7f8f9faffda000c03010002110311003f00ae" +
													"3a5140e828a0028a28a0028a28a0028a28a00296928a005a28a2800a28a2800a28a280128a5a4a00" +
													"28a28a0028a28a0028a28a0028a28a0028a28a0028a28a0028a28a0028a28a0028a28a0028a28a00" +
													"2928a2800a28a2800a28a2800a28a280147414503a0a5a004a28a2800a28a2800a28a5a0028a28a0" +
													"02969296800a4a5a4a0028a5a280128a28a004a2968a004a28a2800a28a5a004a29692800a28a280" +
													"0a28a2800a28a2800a28a2800a28a2800a28a280128a5a4a0028a28a0028a28a0028a28a0070e828" +
													"a0741450014514b400945145001451450014514b40051451400514b49400514514009452d1400945" +
													"145001451450014514500145145002514b49400514b4500145145002514b45002514514005145140" +
													"0514514009452d25001451450014514500387414b483a0a5a0028a29680128a5a4a0028a28a0028a" +
													"2968012968a5a004a2968a004a2969280128a5a280128a5a4a0028a5a2801292968a004a28a2800a" +
													"28a2800a2968a004a28a2800a4a5a280128a28a0028a28a0028a28a0028a28a004a28a2800a28a28" +
													"01c3a0a5a41d052d002d1451400514b45002514b45002514b45001452d0050018a314ec518a006e2" +
													"929d8a31400dc514b8a31400da29d8a4c5002514b450025252d14009452d14009452d14009451450" +
													"02514b49400514514009452d14009451450014514500145145002514b45002514b450028e8296907" +
													"414b40052d140a00514b494ea0031498a7518a006e29714ec518a006e2940a7014a05002628c53c0" +
													"a5c500478a31526da4c500478a4c5498a4c500478a314fc5262801b8a4a7629314009498a7628c50" +
													"0371453a92801292969280128a5a280128a5a2801292968a004a2968a004a4a5a280128a5a280128" +
													"a28a0028a28a0028a28a0051d052d20e829680168a296800a70a6d3850028a7520a70a00314b8a50" +
													"29c0500340a50b4f0b4e0b400c0b4edb4f02968023db46da928a0088ad34ad4d8a42b4010914d22a" +
													"52b4d2280222293152114dc500371494ea4a006d14b494009494ea4a004a4a7518a006d2e2971462" +
													"801b8a314fc518a006628a762931400da29d8a4c500368a5a280128a28a004a29692800a28a28014" +
													"741f4a5a41d052d002d2d252d0028a514829c2801453c534538500380a78148b5228a0000a751450" +
													"01451450014514500145145002119a6115252119a00848a69152114c3400c34869c69314086e2929" +
													"d8a422801b4629d462801b8a5c52e2940a063714ec5380a7014011e28db52eda36d0044569a454c5" +
													"690ad00424537152914c228019494e22928012929692800a4a5a4a0028a28a0070e8296917a0a5a0" +
													"029692968014538520a70a0070a70a68a78a007a8a9074a628a92800a28a2800a28a2800a28a2800" +
													"a28a2800a28a28018c2986a53d2a3340119a294d25020c534d3a9a6801b4b4528a0000a7014014e0" +
													"2818014f0b4aa29d4008168db4ea28018569a56a5a422802022a322a7615130a00888a69a7b0a61a" +
													"006d253a92801292968a004a28a2801cbd052d22fdd1f4a5a005a51494a280145385345385003854" +
													"8b518a91680245a7d3169f400514514005145140051451400514514005145140094c6a92a36a0061" +
													"a4a534da0414d34b9a4a0029452528a0070a914530548b40c70a5a28a0028a28a0028a28a006b0a8" +
													"5854e7a544d4010b5466a56a8cd0030d2529a4a004a28a280128a5a280157ee8a5a45fba29680169" +
													"45251400e14e14d14a2801e2a45a88548b4012ad3ea3534fa005a28a2800a28a2800a28a2800a28a" +
													"2800a28a2801298d4f3d2a36a0069a61a71a69a004a28a4a042d28a6d385031e2a55a885489400fa" +
													"28a2800a28a2800a28a28010f4a89aa53d2a26a0089aa335235466801a69b4e34da004a28a2800a2" +
													"8a28014741f4a5a68e8296801696928a007528a6d28a0078a783518a78a00954d4a0f150a9a914d0" +
													"03e8a28a0028a28a0028a28a0028a28a0028a29280118d4669cc6986801a69a694d2500252514500" +
													"2d28a6d283400f06a45351034f068026a5a62b53a80168a28a0028a29a4d00231a898d3d8d44c680" +
													"1ad519a731a69a0069a4a5a4a004a28a280128a28a0051d052d20e8296800a5a4a5a005a51494a28" +
													"01c29c29829c0d00480d3c1a881a703401306a75440d383500494520345002d14514005149485a80" +
													"169a4d216a693400134d268269a4d0021a434a69a6800a4a2928016969b466801e0d381a8c1a7034" +
													"012834f0d50834a1a8027dd46ea8b751ba80242d4c2d4d2d4d2d400a4d309a09a613400134d34134" +
													"8680128a292800a28a4a0028a28a0051d05140e8296800a5a4a5a005a292968016941a6d2e6801e0" +
													"d2834ccd2834012034e06a2069c0d004a1a9435440d2e68025dd4bbaa2cd19a0090b5349a6e69a4d" +
													"00389a4cd349a4cd003b3499a6e68cd0029a69a5cd250025252d2500145251400b4b9a6d2e6801e0" +
													"d2e6a3cd2e68024cd19a8f3466801f9a4269b9a42680149a69346692800a4a28a002928a2800a4a5" +
													"a4a0028a28a0051d052d20e8296800a5a4a280168a4a2801696928a005a5a4a2801d9a5cd368a007" +
													"e6941a6668cd00499a334ccd2e6801d9a426933484d002e69334949400b9a334946680168a4cd140" +
													"0b49451400945149400b45251400b45251400b9a2928a005a4a28a002928a4a005a4a28a0028a28a" +
													"002928a2800a28a280147414b483a0a5a0028a28a0028a28a005a29296800a28a280168a4a280169" +
													"6928a005a5a4a2801d4868a2801292834500145251400b45251400b45252d0014514940051451400" +
													"514945002d1494500145145001494b494005145140051494500145145001451450028e8296907414" +
													"b4005145140051451400b45145001451450014514b4005145140051451400b4514500252538d2500" +
													"2514514005145140051494b40051451400514514005252d140094514500145145001494b49400514" +
													"514005252d2500145145001451450028e828a074145002d1494b40051451400b45252d0014514500" +
													"14b494b40051451400b45145001451450006929690d0025145140051451400514514005145140051" +
													"452d002514b45002514b450025252d14009452d25002514b49400514514005252d1400945145007f" +
													"ffd9a4a004a28a280128a5a280157ee8a5a45fba2968016945251400e14e14d14a2801e2a45a8854" +
													"8b4012ad3ea3534fa005a28a2800a28a2800a28a2800a28a2800a28a2801298d4f3d2a36a0069a61" +
													"a71a69a004a28a4a042d28a6d385031e2a55a885489400fa28a2800a28a2800a28a28010f4a89aa5" +
													"3d2a26a0089aa335235466801a69b4e34da004a28a2800a28a28014741f4a5a68e8296801696928a" +
													"007528a6d28a0078a783518a78a00954d4a0f150a9a914d003e8a28a0028a28a00"
													).append("</CarImage>");
        strBuf.append("         <Source>PC90-PC</Source>\n");
        strBuf.append("         <HitID></HitID>\n");
        strBuf.append("         <HotID></HotID>\n");
        strBuf.append("         <CameraName>").append(cameraName).append("</CameraName>\n");
        strBuf.append("         <Accuracy>100</Accuracy>\n");
        strBuf.append("         <HeightCharacter>24</HeightCharacter>\n");
        strBuf.append("      </Record>\n");
        strBuf.append("   </InfoCar>\n");
        strBuf.append("</PlateExport>\n");
        return strBuf.toString();
    }

    private static String getImageBase64(String plate)
        throws IOException
    {
        BufferedImage img = new BufferedImage(172, 64, 1);
        Graphics2D g2d = img.createGraphics();
        g2d.setPaint(Color.WHITE);
        g2d.setFont(new Font("Serif", 1, 20));
        FontMetrics fm = g2d.getFontMetrics();
        int x = img.getWidth() - fm.stringWidth(plate) - 5;
        int y = fm.getHeight();
        g2d.drawString(plate, x, y);
        g2d.dispose();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", bos);
        bos.close();
        return (new StringBuilder("0x")).append(adapter.marshal(bos.toByteArray())).toString();
    }

	public static String getCarImageBase64() {
		return CAR_IMAGE_BASE64;
	}

}
