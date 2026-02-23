package com.ransomlab.models;

import java.util.ArrayList;
import java.util.List;

public class AttackRepository {

    public static List<Attack> getAll() {
        List<Attack> list = new ArrayList<>();

        list.add(new Attack(
            "wannacry", "WannaCry", 2017, "#FF2D55",
            "EternalBlue SMB Worm",
            "Network · Unpatched SMBv1",
            "~$4 Billion",
            "200,000+ across 150 countries",
            "$300–$600 BTC",
            "AES-128 + RSA-2048",
            "Lazarus Group (North Korea)",
            "Exploited the NSA's stolen EternalBlue exploit targeting unpatched " +
            "Windows SMBv1. Self-propagating worm that spread globally in hours, " +
            "hitting NHS hospitals, Telefónica, and FedEx. A kill-switch domain " +
            "registration accidentally halted the spread.",
            new String[]{
                "Scanning for SMBv1 port 445...",
                "Sending EternalBlue exploit payload...",
                "Installing DoublePulsar backdoor...",
                "Dropping WNCRY encryptor module...",
                "Spawning worm — spreading laterally...",
                "Encrypting files with AES-128...",
                "RSA-2048 key exchange with C2 server...",
                "Deploying @WanaDecryptor@ ransom note..."
            }, false
        ));

        list.add(new Attack(
            "notpetya", "NotPetya", 2017, "#FF6B35",
            "MBR Wiper / Destroyer",
            "Supply Chain · MEDoc Update",
            "$10+ Billion",
            "Maersk, Merck, Rosneft, FedEx",
            "N/A — Wiper, no recovery",
            "Salsa20 + RSA-2048 (non-recoverable)",
            "Sandworm / Russian GRU",
            "Disguised as ransomware but a destructive wiper. Spread via a " +
            "trojanized MEDoc accounting software update. Overwrites MBR " +
            "permanently — no decryption possible. Used as a cyberweapon " +
            "against Ukraine, causing massive global collateral damage.",
            new String[]{
                "MEDoc update server compromised...",
                "Trojanized update pushed to 400k+ clients...",
                "Harvesting credentials with Mimikatz...",
                "Lateral movement via PSEXEC & WMI...",
                "EternalBlue for unpatched hosts...",
                "Overwriting Master Boot Record (MBR)...",
                "Corrupting NTFS structures...",
                "Scheduling forced reboot — system destroyed..."
            }, true
        ));

        list.add(new Attack(
            "revil", "REvil / Sodinokibi", 2019, "#FF9A00",
            "RaaS Double Extortion",
            "RDP Brute Force · Phishing · Supply Chain",
            "$200+ Million",
            "Kaseya VSA, JBS Foods, Travelex",
            "$70M BTC (Kaseya demand)",
            "AES-256-CBC + Curve25519",
            "Unknown (Russia-linked)",
            "One of the most sophisticated Ransomware-as-a-Service operations. " +
            "Pioneered double extortion — encrypting and exfiltrating data. " +
            "Operated a leak site 'Happy Blog'. Kaseya attack hit 1,500 " +
            "businesses through MSP supply chain in July 2021.",
            new String[]{
                "Gaining initial access via RDP...",
                "Deploying REvil loader (agent.exe)...",
                "Disabling Volume Shadow Copies...",
                "Exfiltrating sensitive data via SFTP...",
                "Uploading stolen data to Happy Blog...",
                "Encrypting with AES-256-CBC...",
                "Key exchange via Curve25519 ECDH...",
                "Dropping ransom note [README.txt]..."
            }, false
        ));

        list.add(new Attack(
            "lockbit", "LockBit", 2019, "#FFD700",
            "Fastest Encryptor · RaaS 3.0",
            "Phishing · Exposed RDP · Affiliates",
            "$91+ Million (US alone)",
            "Boeing, ION Group, ICBC, UK Royal Mail",
            "$50K–$50M per victim",
            "AES-256 + RSA-2048 (multithreaded)",
            "LockBit Gang (Eastern Europe)",
            "The most prolific ransomware gang of 2022-2023. Known for extreme " +
            "speed (encrypts 53GB in 4 min), StealBit exfiltration tool, and " +
            "LockBit 3.0 which introduced a bug bounty program. Taken down by " +
            "Operation Cronos in Feb 2024 but quickly resumed.",
            new String[]{
                "Affiliate gaining RDP/VPN access...",
                "Deploying StealBit exfiltration tool...",
                "Stealing data before encryption...",
                "Terminating AV & backup processes...",
                "Launching multi-threaded encryptor...",
                "Encrypting partial files at 4,096 bytes...",
                "Changing wallpaper to ransom note...",
                "Posting victim on LockBit leak site..."
            }, false
        ));

        list.add(new Attack(
            "ryuk", "Ryuk", 2018, "#AAFF00",
            "Big Game Hunting · BazarLoader",
            "Spear Phishing · TrickBot dropper",
            "$150+ Million",
            "Universal Health Services, Tribune",
            "$100K–$12.5M per target",
            "RSA-4096 + AES-256",
            "Wizard Spider (Russia/CIS)",
            "Targets large enterprises and critical infrastructure for maximum " +
            "ransom. Delivered via TrickBot or Emotet. Known for hospital attacks " +
            "during COVID-19. Requires manual operator deployment — highly targeted. " +
            "Evolved into Conti ransomware.",
            new String[]{
                "Emotet/TrickBot gaining foothold...",
                "Operator connecting via RDP tunnel...",
                "Running ADFind to map Active Directory...",
                "Disabling Windows Defender & backups...",
                "Injecting into svchost.exe process...",
                "Deploying Ryuk to all domain hosts...",
                "Encrypting with RSA-4096 + AES-256...",
                "Leaving RyukReadMe ransom note..."
            }, false
        ));

        list.add(new Attack(
            "kaseya", "Kaseya VSA", 2021, "#00FF88",
            "Supply Chain · MSP Software",
            "Zero-day in Kaseya VSA server",
            "$70M demanded / ~$0 recovered",
            "1,500+ businesses in 17 countries",
            "$70M BTC (group demand)",
            "AES-256 + RSA-2048 (REvil)",
            "REvil / Sodinokibi",
            "REvil exploited a zero-day auth bypass (CVE-2021-30116) in Kaseya " +
            "VSA remote management software. By compromising MSPs (IT providers), " +
            "they hit downstream customers en masse. One of the largest supply " +
            "chain attacks ever. Universal decryptor later obtained by FBI.",
            new String[]{
                "Exploiting Kaseya VSA zero-day auth bypass...",
                "Uploading malicious VSA agent update...",
                "Pushing ransomware to 1,500+ endpoints...",
                "Disabling Windows Defender via GPO...",
                "Deploying REvil encryptor as Kaseya agent...",
                "Mass-encrypting managed endpoints...",
                "Dropping ransom notes across all clients...",
                "Demanding $70M for universal decryptor..."
            }, false
        ));

        list.add(new Attack(
            "jbs", "JBS Foods", 2021, "#00FFD4",
            "Critical Infrastructure · REvil",
            "Compromised credentials / RDP",
            "$11 Million paid",
            "Largest global meat processor",
            "$11M USD paid in BTC",
            "AES-256-CBC (REvil)",
            "REvil / Sodinokibi",
            "REvil attacked JBS — the world's largest meat processing company — " +
            "shutting down plants across USA, Australia, and Canada. The attack " +
            "threatened the global food supply chain. JBS paid $11M ransom. " +
            "Occurred just weeks after Colonial Pipeline attack.",
            new String[]{
                "Compromised JBS VPN credentials...",
                "Lateral movement across OT/IT network...",
                "Identifying production control systems...",
                "Exfiltrating operational data...",
                "Shutting down plant control servers...",
                "Encrypting Windows & Linux servers...",
                "5,000+ workers unable to operate plants...",
                "Paying $11M ransom for decryptors..."
            }, false
        ));

        list.add(new Attack(
            "colonial", "Colonial Pipeline", 2021, "#00AAFF",
            "Critical Infrastructure · DarkSide",
            "Compromised VPN password (Dark Web)",
            "$4.4M paid ($2.3M recovered by FBI)",
            "45% of US East Coast fuel supply",
            "$4.4M BTC (partial recovered)",
            "ChaCha20 + RSA-4096",
            "DarkSide (Russia-linked RaaS)",
            "DarkSide hit Colonial Pipeline, operator of the largest US fuel " +
            "pipeline (5,500 miles). Colonial shut operations proactively, " +
            "causing fuel shortages across the US East Coast. Biden declared " +
            "national emergency. FBI later recovered $2.3M of ransom.",
            new String[]{
                "Breaching via leaked VPN password...",
                "No MFA on legacy VPN account...",
                "Enumerating pipeline OT network...",
                "Exfiltrating 100GB of business data...",
                "Deploying DarkSide ransomware...",
                "Encrypting billing & IT systems...",
                "Colonial shuts 5,500-mile pipeline...",
                "National emergency declared — $4.4M paid..."
            }, false
        ));

        list.add(new Attack(
            "blackbasta", "Black Basta", 2022, "#4488FF",
            "Double Extortion · Conti Successor",
            "QakBot · Phishing · Exposed RDP",
            "$100+ Million in first year",
            "ABB, Rhein-Neckar Hospital, Capita",
            "$500K–$2M average",
            "ChaCha20 + RSA-4096",
            "Former Conti members (Russia)",
            "Black Basta emerged after Conti disbanded in 2022, widely believed " +
            "to be Conti members. Uses QakBot for initial access, ExByte for " +
            "exfiltration. Targets healthcare, manufacturing. Has Windows + " +
            "Linux (ESXi) encryptors for maximum impact.",
            new String[]{
                "QakBot phishing email delivered...",
                "QakBot installing Cobalt Strike beacon...",
                "Operator conducting manual recon...",
                "ExByte tool exfiltrating data...",
                "Disabling EDR via custom kernel driver...",
                "Deploying Black Basta to all hosts...",
                "Encrypting ESXi VMware servers too...",
                "Data posted on Black Basta leak site..."
            }, false
        ));

        list.add(new Attack(
            "akira", "Akira", 2023, "#AA44FF",
            "Modern RaaS · SSH Tunnel C2",
            "Compromised Cisco VPN · No MFA",
            "$42 Million in first year",
            "Stanford, Nissan, 250+ orgs",
            "$200K–$4M per victim",
            "AES-256-CTR + RSA-4096",
            "Unknown (Russia/CIS linked)",
            "Akira emerged in 2023 as a sophisticated RaaS targeting SMBs and " +
            "enterprises. Exploits Cisco VPN vulnerabilities (CVE-2023-20269). " +
            "Uses SSH tunnels for C2 communication. Has both Windows and Linux " +
            "encryptors. Named files with .akira extension.",
            new String[]{
                "Exploiting Cisco VPN CVE-2023-20269...",
                "Bypassing MFA via session hijack...",
                "Establishing SSH tunnel for C2...",
                "Living-off-the-land with PowerShell...",
                "Credential harvesting via LSASS dump...",
                "Disabling AV via safe mode reboot...",
                "Encrypting with AES-256-CTR...",
                "Files renamed with .akira extension..."
            }, false
        ));

        return list;
    }
}
