package com.example.focusguard;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class FocusGuardVpnService extends VpnService implements Runnable {

    private static final String TAG = "FocusGuardVpnService";
    public static volatile boolean isRunning = false;

    private Thread vpnThread;
    private ParcelFileDescriptor vpnInterface;
    private DatagramChannel dnsTunnel;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Serviço VPN INICIADO!");
        if (vpnThread == null) {
            vpnThread = new Thread(this, "VpnThread");
            vpnThread.start();
        }
        isRunning = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Serviço VPN DESTRUÍDO!");
        isRunning = false;
        if (vpnThread != null) {
            vpnThread.interrupt();
        }
        closeVpnInterface();
    }

    @Override
    public void run() {
        try {
            vpnInterface = establishVpnInterface();
            if (vpnInterface == null) {
                Log.e(TAG, "Falha ao estabelecer a interface VPN.");
                return;
            }
            // Nota: A interface VPN foi criada com sucesso.
            Log.d(TAG, "Interface VPN estabelecida.");

            FileInputStream in = new FileInputStream(vpnInterface.getFileDescriptor());
            FileOutputStream out = new FileOutputStream(vpnInterface.getFileDescriptor());
            ByteBuffer packet = ByteBuffer.allocate(32767);

            // Nota: Criando um túnel seguro para o servidor de DNS do Google.
            dnsTunnel = DatagramChannel.open();
            dnsTunnel.connect(new InetSocketAddress("8.8.8.8", 53));
            protect(dnsTunnel.socket());
            Log.d(TAG, "Túnel de DNS para 8.8.8.8 criado e protegido.");

            // Loop principal para ler os pacotes de rede.
            while (!Thread.currentThread().isInterrupted()) {
                int length = in.read(packet.array());
                if (length > 0) {
                    packet.limit(length);

                    ByteBuffer packetForHeaders = packet.duplicate();
                    IP4Header ipHeader = new IP4Header(packetForHeaders);

                    // Só nos importam pacotes UDP.
                    if (ipHeader.protocol == IP4Header.Protocol.UDP) {
                        UDPHeader udpHeader = new UDPHeader(packetForHeaders);

                        // E, especificamente, pacotes para a porta 53 (DNS).
                        if (udpHeader.destinationPort == 53) {
                            ByteBuffer dnsPayload = packetForHeaders.slice();
                            DnsPacket dnsPacket = DnsPacket.fromBytes(dnsPayload);

                            if (dnsPacket != null && !dnsPacket.questions.isEmpty()) {
                                String domain = dnsPacket.questions.get(0).domain;
                                Log.d(TAG, "Pedido de DNS para: " + domain);

                                if (isDomainBlocked(domain)) {
                                    // Nota: Domínio bloqueado. O pacote é descartado.
                                    Log.w(TAG, "BLOQUEADO: " + domain);
                                } else {
                                    // Nota: Domínio permitido. Encaminhando para o DNS real.
                                    packet.rewind();
                                    dnsTunnel.write(packet);
                                    packet.clear();
                                    dnsTunnel.read(packet);
                                    out.write(packet.array(), 0, packet.position());
                                }
                            }
                        }
                    }
                    // Nota: Qualquer outro tipo de pacote (TCP, ICMP, etc.) é ignorado aqui.
                    packet.clear();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro na conexão VPN", e);
        } finally {
            closeVpnInterface();
            stopSelf();
        }
    }

    private boolean isDomainBlocked(String domain) {
        // Lógica de bloqueio simples para teste. No futuro, isto virá de uma lista do utilizador.
        if (domain == null) return false;
        // Usamos .endsWith para bloquear o domínio principal e todos os seus subdomínios
        return domain.endsWith("tiktok.com") || domain.endsWith("youtube.com");
    }

    private ParcelFileDescriptor establishVpnInterface() {
        try {
            Builder builder = new Builder();
            builder.setSession(getString(R.string.app_name));
            builder.addAddress("10.0.0.2", 24);
            // ...
// REMOVA a rota antiga ou comente-a
// builder.addRoute("0.0.0.0", 0);

// ADICIONE a rota específica para o servidor DNS que você usa
            builder.addRoute("8.8.8.8", 32);

            builder.addDnsServer("8.8.8.8");
// ...
            builder.setMtu(1500);
            return builder.establish();
        } catch (Exception e) {
            Log.e(TAG, "Erro ao estabelecer a interface VPN", e);
            return null;
        }
    }

    private void closeVpnInterface() {
        if (vpnInterface != null) {
            try {
                vpnInterface.close();
                vpnInterface = null;
            } catch (IOException e) {
                Log.e(TAG, "Erro ao fechar a interface VPN", e);
            }
        }
        if (dnsTunnel != null) {
            try {
                dnsTunnel.close();
                dnsTunnel = null;
            } catch (IOException e) {
                Log.e(TAG, "Erro ao fechar o túnel DNS", e);
            }
        }
    }
}
