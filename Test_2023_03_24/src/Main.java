import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
	static List<Article> articles = new ArrayList<>();
	static List<Member> members = new ArrayList<>();
	static Member loginedMember = null;
	static int lastArticleId = 3;
	static int lastMemberId = 3;

	public static void main(String[] args) {

		System.out.println("==프로그램 시작==");
		makeArticleTestData();
		makeMemberTestData();

		Scanner sc = new Scanner(System.in);

		while (true) {
			System.out.print("명령어>");
			String command = sc.nextLine().trim();

			if (command.length() == 0) {
				System.out.println("명령어를 입력해주세요");
			}

			if (command.equals("exit")) {
				break;
			}

			if (command.equals("member join")) {

				if (loginedMember != null) {
					System.out.println("로그아웃후에 이용해주세요");
					continue;
				}

				int id = lastMemberId + 1;
				String loginId = null;
				while (true) {
					System.out.println("로그인 아이디 : ");
					loginId = sc.nextLine();

					if (isJoinableLoginId(loginId) == true) {
						System.out.println("이미 가입된 회원이 있습니다");
						continue;
					}
					break;
				}

				String loginPw = null;
				while (true) {
					System.out.println("로그인비밀번호 : ");
					loginPw = sc.nextLine();
					System.out.println("로그인 비밀번호 확인 : ");
					String loginPwConfrim = sc.nextLine();

					if (loginPw.equals(loginPwConfrim) == false) {
						System.out.println("비밀번호를 확인해주세요");
						continue;
					}
					break;
				}

				System.out.println("이름 : ");
				String name = sc.nextLine();

				Member member = new Member(id, loginId, loginPw, name);
				members.add(member);
				System.out.printf("%d번회원가입되었습니다\n",id);
				lastMemberId++;

			} else if (command.equals("member login")) {

				if (loginedMember != null) {
					System.out.println("로그아웃후에 이용해주세요");
					continue;
				}

				System.out.println("로그인 아이디 : ");
				String loginId = sc.nextLine();
				System.out.println("로그인 비밀번호 : ");
				String loginPw = sc.nextLine();

				Member member = getLoginId(loginId);

				if (member == null) {
					System.out.println("일치하는 회원이 없습니다");
					continue;
				}

				if (loginPw.equals(member.loginPw) == false) {
					System.out.println("비밀번호를 확인해주세요");
					continue;
				}

				loginedMember = member;
				System.out.println("로그인되었습니다");

			} else if (command.equals("member logout")) {
				if (loginedMember == null) {
					System.out.println("로그인후에 이용해주세요");
					continue;
				}

				loginedMember = null;
				System.out.println("로그아웃되었습니다");
			} else if (command.equals("article write")) {

				if (loginedMember == null) {
					System.out.println("로그인후에 이용해주세요");
					continue;
				}

				int id = lastArticleId + 1;
				System.out.println("제목 : ");
				String regDate = Util.getDateTimeNowstr();
				String title = sc.nextLine();
				System.out.println("내용 : ");
				String body = sc.nextLine();
				Article article = new Article(id, title, body, regDate, regDate, loginedMember.id);
				articles.add(article);

				System.out.printf("%d번 게시글이 작성되었습니다\n", id);
				lastArticleId++;
			} else if (command.equals("article list")) {
				if (articles.size() == 0) {
					System.out.println("게시글이 존제하지 않습니다");
					continue;
				}

				System.err.println("번호  //  제목  //  조회  //  작성자");

				for (int i = articles.size() - 1; i >= 0; i--) {
					Article article = articles.get(i);
					String writerName = null;
					for (Member member : members) {
						if (member.id == article.id) {
							writerName = member.name;
						}
					}

					System.out.printf("%d  //  %s  // %d  // %s\n", article.id, article.title, article.hit, writerName);
				}

			} else if (command.startsWith("article detail")) {
				String[] cmdDiv = command.split(" ");

				if (cmdDiv.length < 3) {
					System.out.println("명령어를 다시 입력해주세요");
					continue;
				}

				int id = Integer.parseInt(cmdDiv[2]);

				Article foundArticle = getArticle(id);

				if (foundArticle == null) {
					System.out.println("존재하지 않는 게시글입니다");
					continue;
				}

				System.out.printf("번호 : %d\n", foundArticle.id);
				System.out.printf("작성날짜 : %s\n",foundArticle.reDate);
				System.out.printf("수정날짜 : %s\n",foundArticle.update);
				System.out.printf("제목 : %s\n", foundArticle.title);
				System.out.printf("내용 : %s\n", foundArticle.body);
				System.out.printf("조회 : %d\n",foundArticle.hit);

			} else if (command.startsWith("article modify")) {
				String[] cmdDiv = command.split(" ");

				if (cmdDiv.length < 3) {
					System.out.println("명령어를 다시 입력해주세요");
					continue;
				}

				int id = Integer.parseInt(cmdDiv[2]);

				Article foundArticle = getArticle(id);

				if (foundArticle == null) {
					System.out.println("존재하지 않는 게시글입니다");
					continue;
				}

				if (loginedMember == null) {
					System.out.println("로그인후에 이용해주세요");
					continue;
				}

				if (loginedMember.id != foundArticle.id) {
					System.out.println("권한이 없습니다");
					continue;
				}

				System.out.println("새 제목 : ");
				String newTitle = sc.nextLine();
				System.out.println("새 내용 : ");
				String newBody = sc.nextLine();

				foundArticle.title = newTitle;
				foundArticle.body = newBody;

				System.out.printf("%d번 게시글을 수정하였습니다\n", foundArticle.id);

			} else if (command.startsWith("article delete")) {
				String[] cmdDiv = command.split(" ");

				if (cmdDiv.length < 3) {
					System.out.println("명령어를 다시 입력해주세요");
					continue;
				}

				int id = Integer.parseInt(cmdDiv[2]);

				Article foundArticle = getArticle(id);

				if (foundArticle == null) {
					System.out.println("존재하지 않는 게시글입니다");
					continue;
				}

				if (loginedMember == null) {
					System.out.println("로그인후에 이용해주세요");
					continue;
				}

				if (loginedMember.id != foundArticle.id) {
					System.out.println("권한이 없습니다");
					continue;
				}

				articles.remove(foundArticle);
				System.out.printf("%d번 게시글을 삭제하였습니다\n", foundArticle.id);
			}

		}

	}

	private static void makeArticleTestData() {
		System.out.println("테스트를 위한 게시글 데이터를 생성합니다");
		articles.add(new Article(1, "제목1", "내용1", Util.getDateTimeNowstr(), Util.getDateTimeNowstr(), 1, 33));
		articles.add(new Article(2, "제목2", "내용2", Util.getDateTimeNowstr(), Util.getDateTimeNowstr(), 3, 22));
		articles.add(new Article(3, "제목3", "내용3", Util.getDateTimeNowstr(), Util.getDateTimeNowstr(), 3, 11));

	}

	private static void makeMemberTestData() {
		System.out.println("테스트를 위한 회원 데이터를 생성합니다");
		members.add(new Member(1, "test1", "test1", "홍길동"));
		members.add(new Member(2, "test2", "test2", "김영희"));
		members.add(new Member(3, "test3", "test3", "임꺽정"));

	}

	private static Member getLoginId(String loginId) {
		int index = getIndexMember(loginId);
		if (index == -1) {
			return null;
		}
		return members.get(index);
	}

	private static boolean isJoinableLoginId(String loginId) {
		int index = getIndexMember(loginId);
		if (index == -1) {
			return false;
		}
		return true;
	}

	private static int getIndexMember(String loginId) {
		int i = 0;
		for (Member member : members) {
			if (member.loginId.equals(loginId)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	private static Article getArticle(int id) {
		int index = getArticleIndex(id);
		if (index == -1) {
			return null;
		}
		return articles.get(index);
	}

	private static int getArticleIndex(int id) {
		int i = 0;
		for (Article article : articles) {
			if (article.id == id) {
				return i;
			}
			i++;
		}
		return -1;
	}

}

class Member {
	int id;
	String loginId;
	String loginPw;
	String name;

	Member(int id, String loginId, String loginPw, String name) {
		this.id = id;
		this.loginId = loginId;
		this.loginPw = loginPw;
		this.name = name;
	}
}

class Article {
	int id;
	String title;
	String body;
	String reDate;
	String update;
	int memberId;
	int hit;

	Article(int id, String title, String body, String regDate, String update, int memberId) {
		this.id = id;
		this.title = title;
		this.body = body;
		this.reDate = regDate;
		this.update = update;
		this.memberId = memberId;
		this.hit = 0;
	}

	Article(int id, String title, String body, String regDate, String update, int memberId, int hit) {
		this.id = id;
		this.title = title;
		this.body = body;
		this.reDate = regDate;
		this.update = update;
		this.memberId = memberId;
		this.hit = hit;
	}
}