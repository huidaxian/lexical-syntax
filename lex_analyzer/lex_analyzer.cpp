#include <iostream>
#define _CRT_SECURE_NO_WARNINGS
#include <stdio.h>
#include <string>
using namespace std;


FILE *inputf;
char lexeme[100];
char c;
int row = 1;
char keyWords[14][10] = {"main", "if", "else", "do", "while", "for", "switch",
                         "case", "int", "double", "float", "long", "void", "goto"};
void error()
{
    cout << "an error occurred in row " << row << ": near " << lexeme;

    exit(1);
}
void initlexeme()
{
    for (int i = 0; i < 100; i++)
    {
        lexeme[i] = '\0';
    }
}
void rmInvisible()
{
    while (c == ' ' || c == '\t' || c == '\n')
    {
        if (c == '\n')
        {
            row++;
        }
        c = getc(inputf);
        if (c == EOF)
            exit(0);
    }
}

void scan()
{
    int state = 0;
    int lexemeNum = 0;
    if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')
    {
        state = 1;

        while ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))
        {
            lexeme[lexemeNum++] = c;
            c = getc(inputf);
        }
    }
    else if (c >= '0' && c <= '9')
    {
        state = 2;
        while (c >= '0' && c <= '9')
        {
            lexeme[lexemeNum++] = c;
            c = getc(inputf);
        }
        if (c == '.')
        {
            state = 3;
            lexeme[lexemeNum++] = c;
            c = getc(inputf);
            if (c >= '0' && c <= '9')
            {
                state = 4;
                while (c >= '0' && c <= '9')
                {
                    lexeme[lexemeNum++] = c;
                    c = getc(inputf);
                }
            }
        }
    }
    else if (c == '>' || c == '<' || c == '=' || c == '!')
    {
        state = 5;
        lexeme[lexemeNum++] = c;
        c = getc(inputf);
        if (c == '=')
        {
            state = 6;
            lexeme[lexemeNum++] = c;
            c = getc(inputf);
        }
    }
    else
    {
        state = 6;
        lexeme[lexemeNum++] = c;
        c = getc(inputf);
    }
    switch (state)
    {
    case 0:
        error();
        break;
    case 1:
        for (int i = 0; i < 14; i++)
        {
            if (strcmp(keyWords[i], lexeme) == 0)
            {
                cout << "<KeyWord[" << i << "]," << keyWords[i] << ">\n";
                return;
            }
        }
        cout << "<"
             << "id"
             << "," << lexeme << ">\n";

        break;
    case 2:
        cout << "<integer," << lexeme << ">\n";
        break;
    case 3:
        error();
        break;
    case 4:
        cout << "<decimal," << lexeme << ">\n";
        break;
    case 5:
        switch (lexeme[0])
        {
        case '>':
            cout << "<greater>\n";
            break;
        case '<':
            cout << "<less>\n";
            break;
        case '=':
            cout << "<assign>\n";
            break;
        case '!':
            cout << "<not>\n";
            break;
        default:
            error();
            break;
        }
        break;
    case 6:
        if (!strcmp(lexeme, ">="))
        {
            cout << "<greater or equal>\n";
            return;
        }
        if (!strcmp(lexeme, "=="))
        {
            cout << "<equal>\n";
            return;
        }
        if (!strcmp(lexeme, "<="))
        {
            cout << "<less or equal>\n";
            return;
        }
        if (!strcmp(lexeme, "!="))
        {
            cout << "<not equal>\n";
            return;
        }
        if (strlen(lexeme) == 1)
        {
            switch (lexeme[0])
            {
            case '(':
                cout << "<open parenthesis>\n";
                return;
            case ')':
                cout << "<close parenthesis>\n";
                return;
            case '+':

                if (c == '+')
                {
                    cout << "<self-increment>\n";
                    c = getc(inputf);
                    return;
                }
                else
                {
                    cout << "<plus>\n";
                    return;
                }
            case '-':

                if (c == '-')
                {
                    cout << "<self-Decrement>\n";
                    c = getc(inputf);
                    return;
                }
                else
                {
                    cout << "<minus>\n";
                    return;
                }
            case '*':

                if (c == '*')
                {
                    cout << "<exp>\n";
                    c = getc(inputf);
                    return;
                }
                else
                {
                    cout << "<mult>\n";
                    return;
                }
            case '/':

                cout << "<Right-slash>\n";
                return;

            case ';':
                cout << "<semicolon>\n";
                return;
            case '"':
                cout << "<Double-quotation-marks>\n";
                return;

            case '#':
                cout << "<pound>\n";
                return;
            case ',':
                cout << "<comma>\n";
                return;
            case '.':
                cout << "<dot>\n";
                return;
            case '{':
                cout << "<open brace>\n";
                return;
            case '}':
                cout << "<close brace>\n";
                return;
            default:
                error();
            }
        }
        break;

    default:
        error();
    }
}

int main()
{
    inputf = fopen("test.txt", "r");
    c = getc(inputf);
    freopen("result.txt", "w", stdout);

    while (1)
    {
        rmInvisible();
        initlexeme();
        scan();
    }

    return 0;
}
