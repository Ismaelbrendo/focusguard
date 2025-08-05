package com.example.focusguard;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class IP4Header {

    public enum Protocol {
        TCP(6), UDP(17), ICMP(1), UNKNOWN(-1);

        private final int number;

        Protocol(int number) {
            this.number = number;
        }

        public static Protocol fromInt(int number) {
            for (Protocol p : values()) {
                if (p.number == number) {
                    return p;
                }
            }
            return UNKNOWN;
        }
    }

    public byte version;
    public byte ihl; // Internet Header Length
    public int headerLength;
    public Protocol protocol;
    public InetAddress sourceAddress;
    public InetAddress destinationAddress;

    public IP4Header(ByteBuffer buffer) {
        // O primeiro byte contém a versão (4 bits) e o IHL (4 bits)
        byte versionAndIhl = buffer.get();
        this.version = (byte) (versionAndIhl >> 4);
        this.ihl = (byte) (versionAndIhl & 0x0F);
        this.headerLength = this.ihl * 4; // IHL é o número de palavras de 32 bits (4 bytes)

        // Ignora os campos que não precisamos (DSCP/ECN, Total Length, Identification, Flags, etc.)
        buffer.position(buffer.position() + 8);

        // O nono byte é o protocolo
        this.protocol = Protocol.fromInt(buffer.get() & 0xFF);

        // Ignora o checksum do cabeçalho
        buffer.position(buffer.position() + 2);

        // Lê os endereços de origem e destino (4 bytes cada)
        byte[] addressBytes = new byte[4];
        buffer.get(addressBytes, 0, 4);
        try {
            this.sourceAddress = InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            // Lidar com o erro, se necessário
        }

        buffer.get(addressBytes, 0, 4);
        try {
            this.destinationAddress = InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            // Lidar com o erro, se necessário
        }

        // Reposiciona o buffer para o início dos dados do próximo nível (ex: UDP)
        buffer.position(this.headerLength);
    }
}