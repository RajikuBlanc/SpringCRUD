package com.rajiku.SpringCRUD;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sample")
public class TestController {
    private JdbcTemplate jdbcTemplate;

    // コンストラクタ
    // @Controllerのようなアノテーションがついている場合はコンストラクタに限り、@Autowiredをつけなくても
    // 自動でDIコンテナからインスタンスを生成してくれる
    public TestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 一覧画面の表示
    // アノテーションでPostの処理かGetの処理かを判断
    // String型メソッドを定義する(メソッド名は任意)
    // 戻り値に表示するページを設定(.htmlなどは省略可能)
    // 修飾子はpublic
    // ModelがView(html)に値を渡す役割を担う
    @GetMapping("/list")
    public String index(Model model) {
        // 実行するSQL文
        String sql = "select * from test_table;";
        // jdbcTemplateのqueryForListメソッドを使用してsql文を実行する
        // 取得したデータをlist変数に格納する
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        // modelのaddAttributeメソッドを使用して、viewに値を渡す
        // 第一引数でview側で使用する名前を設定
        // 第二引数でどの値を渡すか指定する
        model.addAttribute("testList", list);
        // sample/indexを表示する
        return "sample/list";
    }

    // 詳細画面
    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable int id) {
        String sql = "select * from test_table where id =" + id;
        Map<String, Object> map = jdbcTemplate.queryForMap(sql);
        model.addAttribute("details", map);
        return "/sample/detail";
    }


    // 新規作成
    // 作成画面を表示する(Get)
    @GetMapping("/create")
    public String getCreate(@ModelAttribute TestForm testForm) {
        return "sample/create";
    }

    // 作成したデータの保存(Post)
    @PostMapping("/create")
    public String postCreate(TestForm testForm) {
        String sql = "insert into test_table(name,old) values (?,?);";
        // 引数の順で?に入る
        jdbcTemplate.update(sql, testForm.getName(), testForm.getOld());
        // 処理完了後ページ遷移
        return "redirect:list";
    }

    // 更新画面の表示
    // 選択された値を初期表示する
    @GetMapping("/edit/{id}")
    public String getEdit(@ModelAttribute TestForm testForm, @PathVariable int id) {
        // idで条件を絞る
        String sql = "select * from test_table where id =" + id;
        // sqlを実行し、mapに格納
        Map<String, Object> map = jdbcTemplate.queryForMap(sql);
        // @ModelAttribute によって、TestFormのインスタンスが生成される。
        // 生成した時点ではフィールドに何も値がセットされていない。
        // そのため、setterを使用して、値をセットしviewに渡す。
        testForm.setId((int) map.get("id"));
        testForm.setName((String) map.get("name"));
        testForm.setOld((int) map.get("old"));
        return "sample/edit";
    }

    @PostMapping("/edit/{id}")
    public String postEdit(TestForm testForm, @PathVariable int id) {
        String sql = "update test_table set name=?,old=? where id =" + id;
        jdbcTemplate.update(sql, testForm.getName(), testForm.getOld());
        return "redirect:/sample/detail/{id}";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        String sql = "delete from test_table where id = " + id;
        jdbcTemplate.update(sql);
        return "redirect:/sample/list";
    }

}
