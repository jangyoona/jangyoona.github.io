$(function () {
    // 모든 메시지 보기 버튼 클릭 시
    document.getElementById('seeAllMessages').addEventListener('click', function(event) {
        event.preventDefault(); // 기본 동작 방지
        event.stopPropagation(); // 드롭다운 메뉴에서 클릭 이벤트 전파 방지
        const hiddenItems = document.querySelectorAll('.dropdown-item.d-none'); // 숨겨진 알림
        hiddenItems.forEach(item => {
            item.classList.remove('d-none'); // 숨김 클래스 제거
        });
        this.style.display = 'none'; // 버튼 숨기기

        // 드롭다운 메뉴 잔여 열림 상태 유지
        $('.dropdown-menu').show(); // 드롭다운 메뉴를 다시 표시
    });
    $('.dropdown-menu').on('click', '.delete-notification', function(event) {
        event.stopPropagation(); // 드롭다운 메뉴에서 클릭 이벤트 전파 방지
        var notificationId = $(this).data('id');
        $.ajax({
            url: '/notifications/delete', // 삭제 처리를 수행할 서버의 엔드포인트 URL
            type: 'POST',
            data: { notificationId: notificationId },
            success: function(response) {
                alert("알림이 삭제되었습니다.");
                location.reload(); // 페이지 새로고침하여 변경 사항 반영
            },
            error: function() {
                alert("삭제에 실패했습니다.");
            }
        });
    });
});


    // empId를 서버에서 가져오기
    function initializeWebSocket(empId) {
        let socket = null;

        // empId가 유효한지 확인
        if (empId > 0) {
            socket = new WebSocket(`ws://localhost:8081/ws?empId=${empId}`);
            // WebSocket 관련 코드 추가
        } else {
            console.error("유효하지 않은 empId입니다.");
        }

    socket.onmessage = function(event) {
      console.log(event.data);
      alert(event.data); // 서버로부터 받은 알림 표시
    };
    socket.onopen = function() {
            console.log("WebSocket connection established");
        };

    socket.onclose = function() {
        console.log("WebSocket connection closed");
    };

    socket.onerror = function(error) {
        console.log("WebSocket error: " + error);
    };
    }

