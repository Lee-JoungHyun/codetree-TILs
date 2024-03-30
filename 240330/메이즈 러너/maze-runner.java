import java.util.*;
import java.io.*;

public class Main {
    private static class Poz {
        int y, x;
        public Poz(int y, int x) {
            this.y = y;
            this.x = x;
        }
        
        @Override
        public String toString() {
            return y + " " + x;
        }
    }

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int N = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());
        int K = Integer.parseInt(st.nextToken());
        int moveSum = 0;

        int[][] map = new int[N][N];

        for (int y = 0; y < N; y++) {
            st = new StringTokenizer(br.readLine());
            for (int x = 0; x < N; x++) {
                map[y][x] = Integer.parseInt(st.nextToken());
            }
        }

        List<Poz> people = new LinkedList<>();

        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int y = Integer.parseInt(st.nextToken()) - 1;
            int x = Integer.parseInt(st.nextToken()) - 1;
            Poz tmp = new Poz(y, x);
            people.add(tmp);
        }

        st = new StringTokenizer(br.readLine());
        int endY = Integer.parseInt(st.nextToken()) - 1;
        int endX = Integer.parseInt(st.nextToken()) - 1;

        for (int tc = 0; tc < K; tc++) {
            // 1. 참가자 이동시기키
            moveSum += move(map, people, endY, endX);

            // 2. 정사각형 좌표 찾기
            int[] square = findSquare(people, endY, endX);
            //System.out.println(Arrays.toString(square));

            // 3. 배열 회전시키기
            int[] nE = turnSquare(square, map, people, endY, endX);
            endY = nE[0];
            endX = nE[1];

            //System.out.println((tc+1) + ": " + moveSum + " 위치: " + endY + " " + endX);
            //System.out.println(Arrays.toString(square));
            //System.out.println(people);
        }
        System.out.println(moveSum);
        System.out.println(endY + " " + endX);
        
    }

    private static int[] turnSquare(int[] square, int[][] map, List<Poz> people, int endX, int endY) {
        int Y = map.length;
        int X = map[0].length;
        int nEX = endX;
        int nEY = endY;
        int[][] nxtMap = new int[Y][X];
        List<Poz> nP = new LinkedList<>();
        
        for (int y = 0; y < Y; y++) {
            nxtMap[y] = Arrays.copyOf(map[y], X);
        }
        
        for (int y = square[1]; y < square[1] + square[0] + 1; y++) {
            for (int x = square[2]; x < square[2] + square[0] + 1; x++) {
                int ny = x;
                int nx = (square[0] - (y - square[1])) + square[1] ;
                //System.out.println(y + " " + x + " goto " + ny + " " + nx);
                nxtMap[ny][nx] = map[y][x] - 1 < 0 ? 0 : map[y][x] - 1 ;
                if (x == endX && y == endY) {
                    nEX = nx;
                    nEY = ny;
                }
                for (int i = 0; i < people.size(); i++ ) {
                    Poz person = people.get(i);
                    if (person.y == y && person.x == x) {
                        nP.add(new Poz(ny, nx));
                        people.remove(i);
                        break;
                    }
                    
                }
            }
        }
       // people.clear();
        for (Poz person : nP){
            people.add(person);
        }
        for (int y = 0; y < Y; y++) {
            map[y] = Arrays.copyOf(nxtMap[y], X);
        }
        return new int[]{nEY, nEX};
    }

    private static int[] findSquare(List<Poz> people, int endY, int endX) {
        int[] square = {Integer.MAX_VALUE, 11, 11};
        //1. 오른쪽 맨 아래 기준 잡아 사각형 만들기 -> 긴쪽 기준 2개 만들 수 있다...?
        for (Poz person : people) {
            int hight = Math.abs(person.y - endY);
            int width = Math.abs(person.x - endX);
            int size = Math.max(hight, width);
            if (square[0] < size) continue;
            // 높이가 더 길때 왼쪽으로 못만들면 오른쪽으로 만들기
            int topY = 0, leftX = 0;
            if (hight > width) {
                topY = Math.min(person.y, endY);
                // 왼쪽으로 만들기
                leftX = Math.max(person.x, endX) - size >= 0 ? Math.max(person.x, endX) - hight : Math.min(person.x, endX) + size;
            }
            // 죄우가 더 길때 위로 못만들면 아래로 만들기
            else {
                leftX = Math.min(person.x, endX);
                topY = Math.max(person.y, endY) - size >= 0 ? Math.max(person.y, endY) - size : Math.min(person.y, endY) + size;
            }
            
            if (size < square[0] || (size == square[0] && topY < square[1]) || (size == square[0] && topY == square[1] && leftX < square[2])) {
                square[0] = size;
                square[1] = topY;
                square[2] = leftX;
            }
        }
        return square;
    }

    private static int move(int[][] map, List<Poz> people, int endY, int endX) {
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};
        int Y = map.length;
        int X = map[0].length;
        List<Poz> nextPeople = new LinkedList<>();
        int moveCnt = 0;

        for (Poz person : people) {
            int preDistance = Math.abs(endY - person.y) + Math.abs(endX - person.x);
            //System.out.println(person + "의 원 거리: " + preDistance);
            // 1. 갈수 있는 방향 찾기 (상하 갈 수 있으면 바로 보내기)
            boolean flag = true;
            for (int d = 0; d < 4; d++) {
                int ny = person.y + dy[d];
                int nx = person.x + dx[d];
                if (0 <= ny && ny < Y && 0 <= nx && nx < X && map[ny][nx] == 0) {
                    int nxtDistance = Math.abs(endY - ny) + Math.abs(endX - nx);
                    if (nxtDistance == 0) {
                        break;
                    } else if (nxtDistance < preDistance) {
                        moveCnt++;
                        nextPeople.add(new Poz(ny, nx));
                        flag = false;    
                        break;
                    }
                }
            }
            if (flag) {
                nextPeople.add(person);
            }
        }
        people = nextPeople;
        //System.out.println(moveCnt + ": " + people);
        return moveCnt;
    }


    private static void print(int[][] map) {
        for (int i = 0; i < map.length; i++)
            System.out.println(Arrays.toString(map[i]));
    }
}