package com.thehalfspace.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다"),

    // Match
    MATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "경기를 찾을 수 없습니다"),

    // Standing
    STANDING_NOT_FOUND(HttpStatus.NOT_FOUND, "순위 정보를 찾을 수 없습니다"),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다"),
    COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "본인이 작성한 댓글만 삭제할 수 있습니다"),
    INVALID_PARENT_COMMENT(HttpStatus.BAD_REQUEST, "대댓글에는 답글을 달 수 없습니다"),

    // Board / Post
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "게시판을 찾을 수 없습니다"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다"),
    POST_FORBIDDEN(HttpStatus.FORBIDDEN, "본인이 작성한 게시글만 수정·삭제할 수 있습니다"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),

    // Auth
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다"),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),

    // AI 분석
    PREVIEW_NOT_READY(HttpStatus.NOT_FOUND, "AI 프리뷰가 아직 생성되지 않았습니다"),
    REVIEW_NOT_READY(HttpStatus.NOT_FOUND, "AI 리뷰가 아직 생성되지 않았습니다"),
    AGENT_EXECUTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "AI 에이전트 실행에 실패했습니다"),
    AGENT_ALREADY_RUNNING(HttpStatus.CONFLICT, "이미 실행 중인 AI 에이전트가 있습니다");

    private final HttpStatus status;
    private final String message;
}
