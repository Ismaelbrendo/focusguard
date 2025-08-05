package com.example.focusguard;

import java.nio.ByteBuffer;

public class UDPHeader {

    public int sourcePort;
    public int destinationPort;
    public int length;
    public int checksum;

    public UDPHeader(ByteBuffer buffer) {
        // Lê os campos de 2 bytes (16 bits) do cabeçalho UDP
        this.sourcePort = buffer.getShort() & 0xFFFF;
        this.destinationPort = buffer.getShort() & 0xFFFF;
        this.length = buffer.getShort() & 0xFFFF;
        this.checksum = buffer.getShort() & 0xFFFF;
    }
}