package com.a303.consultms.domain.channel.service;

import com.a303.consultms.domain.channel.Channel;
import com.a303.consultms.domain.channel.dto.response.ChannelRes;
import com.a303.consultms.domain.channel.repository.ChannelRepository;
import com.a303.consultms.domain.member.dto.response.MemberBaseRes;
import com.a303.consultms.global.client.MemberClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;
    //    private final ConsultRepository consultRepository;
    private final MemberClient memberClient;

    @Override
    public String registerPersonalChat(int receiverId, int memberId) {
        Map<String, String> receiver = new HashMap<>();
        //receiver 정보 받아오기
        MemberBaseRes receiverMember = memberClient.getMember(receiverId).getResult();

        receiver.put("memberId", String.valueOf(receiverId));
        receiver.put("nickname", receiverMember.nickname());

        Map<String, String> sender = new HashMap<>();
        //sender 정보 받아오기
        MemberBaseRes senderMember = memberClient.getMember(memberId).getResult();

        sender.put("memberId", String.valueOf(memberId));
        sender.put("nickname", senderMember.nickname());

        Channel channel = Channel.createChannel(receiver, sender, new ArrayList<>());

        channelRepository.save(channel);

        return channel.getChannelId();
    }

    //참여중인 채팅방 목록 조회
    @Override
    public List<ChannelRes> getPersonalChatList(int sender) {

        List<Channel> channelList = channelRepository.findListBySenderOrReceiver(
            String.valueOf(sender)
        );

        List<ChannelRes> channelResList = new ArrayList<>();

        for (Channel channel : channelList) {
            int senderId = Integer.parseInt(channel.getSender().get("memberId"));
            int receiverId = Integer.parseInt(channel.getReceiver().get("memberId"));

            int other = (sender == senderId) ? receiverId : senderId;

            MemberBaseRes receiverInfo = memberClient.getMember(receiverId).getResult();
            MemberBaseRes senderInfo = memberClient.getMember(senderId).getResult();


            //TODO: 탈퇴한 사용자 처리

            ChannelRes channelRes = ChannelRes.builder()
                .channelId(channel.getChannelId())
                .receiver(receiverInfo)
                .sender(senderInfo)
                .build();

            channelResList.add(channelRes);

        }

        return channelResList;
    }

    //개인 채팅 조회
    @Override
    public Channel readPersonalChat(String channelId, int memberId) {

        Channel channel = channelRepository.findById(channelId).get();
        System.out.println(channel);

        Map<String, String> sender = channel.getSender();
        System.out.println(sender);

        if(memberId != Integer.parseInt(sender.get("memberId"))){
            channel.setSender(channel.getReceiver());
            channel.setReceiver(sender);
        }

//        int receiverId = Integer.parseInt(channel.getReceiver().get("memberId"));

        // TODO: 탈퇴한 사용자 처리


        return channel;
    }


    @Override
    public Channel readPersonalChatByRecevier(int receiver, int memberId) {
        Channel channel = channelRepository.findBySenderOrReceiver(String.valueOf(receiver),
            String.valueOf(memberId));

        return channel;
    }

    //고민상담소 입장(채널생성)
//    @Override
//    public String enterConsultingRoom(int consultId, int memberId) throws BaseExceptionHandler {
//
//        consultException(consultId);
////        memberException(memberId);
//
//        List<Message> messageList = new ArrayList<>();
//
//        //채널 생성
//        Channel channel = Channel.createChannel(consultId, memberId, false, false, messageList);
//
//        channelRepository.save(channel);
//
//        return channel.getId();
//    }

    //회원 존재여부 유효성 검사
    private void memberException(int memberId) {
        //멤버 존재하는지 유효성 검사 추가하기
    }

    //고민상담소 존재여부 유효성 검사
//    private void consultException(int consultId) {
//        if (consultRepository.findConsultByConsultId(consultId) == null) {
//            throw new BaseExceptionHandler(ErrorCode.NOT_FOUND_CONSULT_EXCEPTION);
//        }
//    }
}
