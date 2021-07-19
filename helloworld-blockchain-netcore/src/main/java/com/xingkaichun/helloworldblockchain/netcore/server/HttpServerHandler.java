package com.xingkaichun.helloworldblockchain.netcore.server;

import com.xingkaichun.helloworldblockchain.netcore.dto.*;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private HttpServerHandlerResolver httpServerHandlerResolver;

	public HttpServerHandler(HttpServerHandlerResolver httpServerHandlerResolver) {
		super();
		this.httpServerHandlerResolver = httpServerHandlerResolver;
	}


	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {

		String requestIp = parseRequestIp(channelHandlerContext);
		String requestApi = parseRequestApi(fullHttpRequest);
		String requestBody = parseRequestBody(fullHttpRequest);

		String responseMessage;
		/*
		 * 任何节点都可以访问这里的接口，请不要在这里写任何能泄露用户私钥的代码。
		 * 因为有的节点没有公网IP，所以为了照顾这些节点，新增了一系列的接口。
		 * 但是我们假设所有节点都有公网IP，我们只需要写五个接口就可以了。
		 * @see com.xingkaichun.helloworldblockchain.netcore.dto.API.PING
		 * @see com.xingkaichun.helloworldblockchain.netcore.dto.API.GET_NODES
		 * @see com.xingkaichun.helloworldblockchain.netcore.dto.API.POST_BLOCKCHAIN_HEIGHT
		 * @see com.xingkaichun.helloworldblockchain.netcore.dto.API.GET_BLOCK
		 * @see com.xingkaichun.helloworldblockchain.netcore.dto.API.POST_TRANSACTION
		 */
		if("/".equals(requestApi)){
			responseMessage = "HelloworldBlockchain";
		}else if(API.PING.equals(requestApi)){
			PingRequest request = JsonUtil.toObject(requestBody, PingRequest.class);
			PingResponse response = httpServerHandlerResolver.ping(requestIp,request);
			responseMessage = JsonUtil.toString(response);
		}else if(API.GET_NODES.equals(requestApi)){
			GetNodesRequest request = JsonUtil.toObject(requestBody, GetNodesRequest.class);
			GetNodesResponse getBlockResponse = httpServerHandlerResolver.getNodes(request);
			responseMessage = JsonUtil.toString(getBlockResponse);
		}else if(API.GET_BLOCK.equals(requestApi)){
			GetBlockRequest request = JsonUtil.toObject(requestBody, GetBlockRequest.class);
			GetBlockResponse getBlockResponse = httpServerHandlerResolver.getBlock(request);
			responseMessage = JsonUtil.toString(getBlockResponse);
		}else if(API.GET_UNCONFIRMED_TRANSACTIONS.equals(requestApi)){
			GetUnconfirmedTransactionsRequest request = JsonUtil.toObject(requestBody, GetUnconfirmedTransactionsRequest.class);
			GetUnconfirmedTransactionsResponse response = httpServerHandlerResolver.getUnconfirmedTransactions(request);
			responseMessage = JsonUtil.toString(response);
		}else if(API.POST_TRANSACTION.equals(requestApi)){
			PostTransactionRequest request = JsonUtil.toObject(requestBody, PostTransactionRequest.class);
			PostTransactionResponse response = httpServerHandlerResolver.postTransaction(request);
			responseMessage = JsonUtil.toString(response);
		}else if(API.POST_BLOCK.equals(requestApi)){
			PostBlockRequest request = JsonUtil.toObject(requestBody, PostBlockRequest.class);
			PostBlockResponse response = httpServerHandlerResolver.postBlock(request);
			responseMessage = JsonUtil.toString(response);
		}else if(API.POST_BLOCKCHAIN_HEIGHT.equals(requestApi)){
			PostBlockchainHeightRequest request = JsonUtil.toObject(requestBody, PostBlockchainHeightRequest.class);
			PostBlockchainHeightResponse response = httpServerHandlerResolver.postBlockchainHeight(requestIp,request);
			responseMessage = JsonUtil.toString(response);
		}else if(API.GET_BLOCKCHAIN_HEIGHT.equals(requestApi)){
			GetBlockchainHeightRequest request = JsonUtil.toObject(requestBody, GetBlockchainHeightRequest.class);
			GetBlockchainHeightResponse response = httpServerHandlerResolver.getBlockchainHeight(request);
			responseMessage = JsonUtil.toString(response);
		}else {
			responseMessage = "404 NOT FOUND";
		}
		writeResponse(channelHandlerContext, responseMessage);
	}

	private String parseRequestBody(FullHttpRequest fullHttpRequest) {
		return fullHttpRequest.content().toString(CharsetUtil.UTF_8);
	}

	private String parseRequestIp(ChannelHandlerContext channelHandlerContext) {
		InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
		String ip = inetSocketAddress.getAddress().getHostAddress();
		return ip;
	}

	private String parseRequestApi(FullHttpRequest fullHttpRequest) {
		String uri = fullHttpRequest.uri();
		if(uri.contains("?")){
			return uri.split("\\?")[0];
		}else {
			return uri;
		}
	}

	private void writeResponse(ChannelHandlerContext channelHandlerContext, String msg) {
		ByteBuf bf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
		FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, bf);
		res.headers().set(HttpHeaderNames.CONTENT_LENGTH, msg.length());
		res.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
		channelHandlerContext.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
	}
}